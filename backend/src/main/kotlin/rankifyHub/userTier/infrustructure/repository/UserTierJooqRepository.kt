package rankifyHub.userTier.infrustructure.repository

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.tables.UserTier as JUserTier
import rankifyHub.tables.UserTierLevel as JUserTierLevel
import rankifyHub.tables.UserTierLevelItem as JUserTierLevelItem
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.UserTierName

@Repository
class UserTierJooqRepository(private val dsl: DSLContext) : UserTierRepository {

  override fun save(userTier: UserTier): UserTier {
    dsl
      // こちらも別名を使って呼び出す
      .insertInto(JUserTier.USER_TIER)
      .set(JUserTier.USER_TIER.ID, userTier.id)
      .set(JUserTier.USER_TIER.ANONYMOUS_ID, userTier.anonymousId.value)
      .set(JUserTier.USER_TIER.CATEGORY_ID, userTier.categoryId)
      .set(JUserTier.USER_TIER.NAME, userTier.name.value)
      .set(JUserTier.USER_TIER.IS_PUBLIC, userTier.isPublic)
      .set(JUserTier.USER_TIER.ACCESS_URL, userTier.accessUrl.value)
      .set(
        JUserTier.USER_TIER.CREATED_AT,
        userTier.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
          ?: LocalDateTime.now()
      )
      .set(
        JUserTier.USER_TIER.UPDATED_AT,
        userTier.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
          ?: LocalDateTime.now()
      )
      .onDuplicateKeyUpdate()
      .set(JUserTier.USER_TIER.NAME, userTier.name.value)
      .set(JUserTier.USER_TIER.IS_PUBLIC, userTier.isPublic)
      .set(JUserTier.USER_TIER.ACCESS_URL, userTier.accessUrl.value)
      .set(JUserTier.USER_TIER.UPDATED_AT, LocalDateTime.now())
      .execute()

    // USER_TIER_LEVEL_ITEM も別名で
    dsl
      .deleteFrom(JUserTierLevelItem.USER_TIER_LEVEL_ITEM)
      .where(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_ID.eq(userTier.id))
      .execute()

    // USER_TIER_LEVEL も別名で
    dsl
      .deleteFrom(JUserTierLevel.USER_TIER_LEVEL)
      .where(JUserTierLevel.USER_TIER_LEVEL.USER_TIER_ID.eq(userTier.id))
      .execute()

    userTier.getLevels().forEach { level ->
      dsl
        .insertInto(JUserTierLevel.USER_TIER_LEVEL)
        .set(JUserTierLevel.USER_TIER_LEVEL.ID, level.id)
        .set(JUserTierLevel.USER_TIER_LEVEL.USER_TIER_ID, userTier.id)
        .set(JUserTierLevel.USER_TIER_LEVEL.NAME, level.name)
        .set(JUserTierLevel.USER_TIER_LEVEL.ORDER_INDEX, level.orderIndex.value)
        .set(
          JUserTierLevel.USER_TIER_LEVEL.CREATED_AT,
          level.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
        )
        .set(
          JUserTierLevel.USER_TIER_LEVEL.UPDATED_AT,
          level.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
        )
        .execute()

      level.items.forEach { item ->
        dsl
          .insertInto(JUserTierLevelItem.USER_TIER_LEVEL_ITEM)
          .set(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ID, item.id)
          .set(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_LEVEL_ID, level.id)
          .set(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_ID, userTier.id)
          .set(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ITEM_ID, item.itemId)
          .set(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ORDER_INDEX, item.orderIndex.value)
          .set(
            JUserTierLevelItem.USER_TIER_LEVEL_ITEM.CREATED_AT,
            item.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
              ?: LocalDateTime.now()
          )
          .set(
            JUserTierLevelItem.USER_TIER_LEVEL_ITEM.UPDATED_AT,
            item.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
              ?: LocalDateTime.now()
          )
          .execute()
      }
    }
    return userTier
  }

  override fun findById(userTierId: UUID): UserTier? {
    // 1. user_tier テーブルから対象の1件を取得
    val userTierRecord =
      dsl.selectFrom(JUserTier.USER_TIER).where(JUserTier.USER_TIER.ID.eq(userTierId)).fetchOne()
        ?: return null

    // 2. user_tier_level テーブルから UserTier に紐づくレベル一覧を取得
    val levelRecords =
      dsl
        .selectFrom(JUserTierLevel.USER_TIER_LEVEL)
        .where(JUserTierLevel.USER_TIER_LEVEL.USER_TIER_ID.eq(userTierId))
        .orderBy(JUserTierLevel.USER_TIER_LEVEL.ORDER_INDEX.asc()) // 必要に応じて並び替え
        .fetch()

    // 3. 各レベルに対して user_tier_level_item テーブルからアイテム一覧を取得
    val levels =
      levelRecords.map { levelRec ->
        val levelId = levelRec[JUserTierLevel.USER_TIER_LEVEL.ID]

        // レベルに紐づくアイテムを取得
        val itemRecords =
          dsl
            .selectFrom(JUserTierLevelItem.USER_TIER_LEVEL_ITEM)
            .where(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_LEVEL_ID.eq(levelId))
            .orderBy(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ORDER_INDEX.asc())
            .fetch()

        // ドメインオブジェクト: UserTierLevelItem を再構築
        val levelItems =
          itemRecords.map { itemRec ->
            // 実際のドメインクラス設計に合わせて再構築
            // 下記は例としてのイメージ
            rankifyHub.userTier.domain.model.UserTierLevelItem.reconstruct(
              id = itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ID],
              userTierLevelId = levelId,
              userTierId = userTierId,
              itemId = itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ITEM_ID],
              orderIndex =
                rankifyHub.userTier.domain.vo.OrderIndex(
                  itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ORDER_INDEX]
                ),
              createdAt =
                itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.CREATED_AT]?.toInstant(
                  ZoneOffset.UTC
                )
                  ?: Instant.now(),
              updatedAt =
                itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.UPDATED_AT]?.toInstant(
                  ZoneOffset.UTC
                )
                  ?: Instant.now()
            )
          }

        // ドメインオブジェクト: UserTierLevel を再構築
        rankifyHub.userTier.domain.model.UserTierLevel.reconstruct(
          id = levelId,
          userTierId = userTierId, // 必要なら保持
          name = levelRec[JUserTierLevel.USER_TIER_LEVEL.NAME],
          orderIndex =
            rankifyHub.userTier.domain.vo.OrderIndex(
              levelRec[JUserTierLevel.USER_TIER_LEVEL.ORDER_INDEX]
            ),
          createdAt = levelRec[JUserTierLevel.USER_TIER_LEVEL.CREATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          updatedAt = levelRec[JUserTierLevel.USER_TIER_LEVEL.UPDATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          items = levelItems
        )
      }

    // 4. userTierRecord から UserTier を再構築し、上記で取得した levels をセット
    return UserTier.reconstruct(
      id = userTierRecord[JUserTier.USER_TIER.ID],
      anonymousId = AnonymousId(userTierRecord[JUserTier.USER_TIER.ANONYMOUS_ID]),
      categoryId = userTierRecord[JUserTier.USER_TIER.CATEGORY_ID],
      name = UserTierName(userTierRecord[JUserTier.USER_TIER.NAME]),
      isPublic = userTierRecord[JUserTier.USER_TIER.IS_PUBLIC],
      accessUrl = AccessUrl(userTierRecord[JUserTier.USER_TIER.ACCESS_URL]),
      createdAt = userTierRecord[JUserTier.USER_TIER.CREATED_AT]?.toInstant(ZoneOffset.UTC)
          ?: Instant.now(),
      updatedAt = userTierRecord[JUserTier.USER_TIER.UPDATED_AT]?.toInstant(ZoneOffset.UTC)
          ?: Instant.now(),
      levels = levels
    )
  }

  override fun findByIsPublicTrueOrderByCreatedAtDesc(): List<UserTier> {
    val records =
      dsl
        .selectFrom(JUserTier.USER_TIER)
        .where(JUserTier.USER_TIER.IS_PUBLIC.eq(true))
        .orderBy(JUserTier.USER_TIER.CREATED_AT.desc())
        .fetch()

    return records.map { rec ->
      UserTier.reconstruct(
        id = rec.get(JUserTier.USER_TIER.ID),
        anonymousId = AnonymousId(rec.get(JUserTier.USER_TIER.ANONYMOUS_ID)),
        categoryId = rec.get(JUserTier.USER_TIER.CATEGORY_ID),
        name = UserTierName(rec.get(JUserTier.USER_TIER.NAME)),
        isPublic = rec.get(JUserTier.USER_TIER.IS_PUBLIC),
        accessUrl = AccessUrl(rec.get(JUserTier.USER_TIER.ACCESS_URL)),
        createdAt = rec.get(JUserTier.USER_TIER.CREATED_AT)?.toInstant(ZoneOffset.UTC)
            ?: Instant.now(),
        updatedAt = rec.get(JUserTier.USER_TIER.UPDATED_AT)?.toInstant(ZoneOffset.UTC)
            ?: Instant.now(),
        levels = emptyList()
      )
    }
  }

  override fun findLatest(limit: Int): List<UserTier> {
    val records =
      dsl
        .selectFrom(JUserTier.USER_TIER)
        .orderBy(JUserTier.USER_TIER.CREATED_AT.desc())
        .limit(limit)
        .fetch()

    return records.map { rec ->
      UserTier.reconstruct(
        id = rec[JUserTier.USER_TIER.ID],
        anonymousId = AnonymousId(rec[JUserTier.USER_TIER.ANONYMOUS_ID]),
        categoryId = rec[JUserTier.USER_TIER.CATEGORY_ID],
        name = UserTierName(rec[JUserTier.USER_TIER.NAME]),
        isPublic = rec[JUserTier.USER_TIER.IS_PUBLIC],
        accessUrl = AccessUrl(rec[JUserTier.USER_TIER.ACCESS_URL]),
        createdAt = rec[JUserTier.USER_TIER.CREATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        updatedAt = rec[JUserTier.USER_TIER.UPDATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        levels = emptyList()
      )
    }
  }

  override fun findSince(timestamp: Instant): List<UserTier> {
    val records =
      dsl
        .selectFrom(JUserTier.USER_TIER)
        .where(
          JUserTier.USER_TIER.CREATED_AT.greaterOrEqual(
            LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)
          )
        )
        .orderBy(JUserTier.USER_TIER.CREATED_AT.desc())
        .fetch()

    return records.map { rec ->
      UserTier.reconstruct(
        id = rec.get(JUserTier.USER_TIER.ID),
        anonymousId = AnonymousId(rec.get(JUserTier.USER_TIER.ANONYMOUS_ID)),
        categoryId = rec.get(JUserTier.USER_TIER.CATEGORY_ID),
        name = UserTierName(rec.get(JUserTier.USER_TIER.NAME)),
        isPublic = rec.get(JUserTier.USER_TIER.IS_PUBLIC),
        accessUrl = AccessUrl(rec.get(JUserTier.USER_TIER.ACCESS_URL)),
        createdAt = rec.get(JUserTier.USER_TIER.CREATED_AT).toInstant(ZoneOffset.UTC)
            ?: Instant.now(),
        updatedAt = rec.get(JUserTier.USER_TIER.UPDATED_AT)?.toInstant(ZoneOffset.UTC)
            ?: Instant.now(),
        levels = emptyList()
      )
    }
  }
}
