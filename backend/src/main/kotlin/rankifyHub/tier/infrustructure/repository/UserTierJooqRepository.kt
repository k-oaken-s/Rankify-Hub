package rankifyHub.tier.infrustructure.repository

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.tables.UserTier as JUserTier
import rankifyHub.tables.UserTierLevel as JUserTierLevel
import rankifyHub.tables.UserTierLevelItem as JUserTierLevelItem
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.model.UserTierLevel
import rankifyHub.tier.domain.model.UserTierLevelItem
import rankifyHub.tier.domain.repository.UserTierRepository
import rankifyHub.tier.domain.vo.AccessUrl
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.UserTierName

/** TierのJOOQ永続化実装 */
@Repository
class UserTierJooqRepository(private val dsl: DSLContext) : UserTierRepository {

  /** Tierとその関連エンティティを保存 */
  override fun save(userTier: UserTier): UserTier {
    dsl
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

    dsl
      .deleteFrom(JUserTierLevelItem.USER_TIER_LEVEL_ITEM)
      .where(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_ID.eq(userTier.id))
      .execute()

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

  /** 指定IDのTierと関連エンティティを取得 */
  override fun findById(userTierId: UUID): UserTier? {
    val userTierRecord =
      dsl.selectFrom(JUserTier.USER_TIER).where(JUserTier.USER_TIER.ID.eq(userTierId)).fetchOne()
        ?: return null

    val levelRecords =
      dsl
        .selectFrom(JUserTierLevel.USER_TIER_LEVEL)
        .where(JUserTierLevel.USER_TIER_LEVEL.USER_TIER_ID.eq(userTierId))
        .orderBy(JUserTierLevel.USER_TIER_LEVEL.ORDER_INDEX.asc())
        .fetch()

    val levels =
      levelRecords.map { levelRec ->
        val levelId = levelRec[JUserTierLevel.USER_TIER_LEVEL.ID]

        val itemRecords =
          dsl
            .selectFrom(JUserTierLevelItem.USER_TIER_LEVEL_ITEM)
            .where(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.USER_TIER_LEVEL_ID.eq(levelId))
            .orderBy(JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ORDER_INDEX.asc())
            .fetch()

        val levelItems =
          itemRecords.map { itemRec ->
            UserTierLevelItem.reconstruct(
              id = itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ID],
              userTierLevelId = levelId,
              userTierId = userTierId,
              itemId = itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ITEM_ID],
              orderIndex = OrderIndex(itemRec[JUserTierLevelItem.USER_TIER_LEVEL_ITEM.ORDER_INDEX]),
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

        UserTierLevel.reconstruct(
          id = levelId,
          userTierId = userTierId,
          name = levelRec[JUserTierLevel.USER_TIER_LEVEL.NAME],
          orderIndex = OrderIndex(levelRec[JUserTierLevel.USER_TIER_LEVEL.ORDER_INDEX]),
          createdAt = levelRec[JUserTierLevel.USER_TIER_LEVEL.CREATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          updatedAt = levelRec[JUserTierLevel.USER_TIER_LEVEL.UPDATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          items = levelItems
        )
      }

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

  /** 全てのTierを作成日時の降順で取得 */
  override fun findAllOrderByCreatedAtDesc(): List<UserTier> {
    val records =
      dsl.selectFrom(JUserTier.USER_TIER).orderBy(JUserTier.USER_TIER.CREATED_AT.desc()).fetch()

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

  /** 作成日時が最新のTierを指定件数取得 */
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

  /** 指定日時以降に作成されたTierを取得する */
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
