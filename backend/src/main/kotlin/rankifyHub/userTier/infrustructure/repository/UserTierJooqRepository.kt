package rankifyHub.userTier.infrastructure.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.UserTierName
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import rankifyHub.tables.UserTier as JUserTier
import rankifyHub.tables.UserTierLevel as JUserTierLevel
import rankifyHub.tables.UserTierLevelItem as JUserTierLevelItem

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
