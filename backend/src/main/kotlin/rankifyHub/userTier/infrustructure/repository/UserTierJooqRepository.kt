package rankifyHub.userTier.infrastructure.repository

import java.time.Instant
import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.jooq.generated.tables.UserTierLevelItemTable
import rankifyHub.jooq.generated.tables.UserTierLevelTable
import rankifyHub.jooq.generated.tables.UserTierTable // 例: jOOQコード生成でできる
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.domain.vo.*

@Repository
class UserTierJooqRepository(private val dsl: DSLContext) : UserTierRepository {

  override fun save(userTier: UserTier): UserTier {
    // 1) user_tier をUpsert
    dsl
      .insertInto(UserTierTable.USER_TIER)
      .set(UserTierTable.USER_TIER.ID, userTier.id.toString())
      .set(UserTierTable.USER_TIER.ANONYMOUS_ID, userTier.anonymousId.value)
      .set(UserTierTable.USER_TIER.CATEGORY_ID, userTier.categoryId.toString())
      .set(UserTierTable.USER_TIER.NAME, userTier.name.value)
      .set(UserTierTable.USER_TIER.IS_PUBLIC, userTier.isPublic)
      .set(UserTierTable.USER_TIER.ACCESS_URL, userTier.accessUrl.value)
      .set(UserTierTable.USER_TIER.IMAGE_PATH, userTier.imagePath)
      .set(UserTierTable.USER_TIER.CREATED_AT, userTier.createdAt)
      .set(UserTierTable.USER_TIER.UPDATED_AT, userTier.updatedAt)
      .onDuplicateKeyUpdate()
      .set(UserTierTable.USER_TIER.ANONYMOUS_ID, userTier.anonymousId.value)
      .set(UserTierTable.USER_TIER.CATEGORY_ID, userTier.categoryId.toString())
      .set(UserTierTable.USER_TIER.NAME, userTier.name.value)
      .set(UserTierTable.USER_TIER.IS_PUBLIC, userTier.isPublic)
      .set(UserTierTable.USER_TIER.ACCESS_URL, userTier.accessUrl.value)
      .set(UserTierTable.USER_TIER.IMAGE_PATH, userTier.imagePath)
      .set(UserTierTable.USER_TIER.UPDATED_AT, userTier.updatedAt)
      .execute()

    // 2) user_tier_level や user_tier_level_item を一旦削除→再Insert するなど
    //    (差分更新する場合はもう少し複雑なロジックが必要)
    dsl
      .deleteFrom(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM)
      .where(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.USER_TIER_ID.eq(userTier.id.toString()))
      .execute()

    dsl
      .deleteFrom(UserTierLevelTable.USER_TIER_LEVEL)
      .where(UserTierLevelTable.USER_TIER_LEVEL.USER_TIER_ID.eq(userTier.id.toString()))
      .execute()

    // 3) LevelsをInsert
    userTier.getLevels().forEach { level ->
      dsl
        .insertInto(UserTierLevelTable.USER_TIER_LEVEL)
        .set(UserTierLevelTable.USER_TIER_LEVEL.ID, level.id.toString())
        .set(UserTierLevelTable.USER_TIER_LEVEL.USER_TIER_ID, userTier.id.toString())
        .set(UserTierLevelTable.USER_TIER_LEVEL.NAME, level.name)
        .set(UserTierLevelTable.USER_TIER_LEVEL.ORDER_INDEX, level.orderIndex.value)
        .set(UserTierLevelTable.USER_TIER_LEVEL.IMAGE_PATH, level.imagePath)
        .set(UserTierLevelTable.USER_TIER_LEVEL.CREATED_AT, level.createdAt)
        .set(UserTierLevelTable.USER_TIER_LEVEL.UPDATED_AT, level.updatedAt)
        .execute()

      // 4) LevelItemsをInsert
      level.items.forEach { item ->
        dsl
          .insertInto(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM)
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.ID, item.id.toString())
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.USER_TIER_LEVEL_ID, level.id.toString())
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.USER_TIER_ID, userTier.id.toString())
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.ITEM_ID, item.itemId.toString())
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.ORDER_INDEX, item.orderIndex.value)
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.IMAGE_PATH, item.imagePath)
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.CREATED_AT, item.createdAt)
          .set(UserTierLevelItemTable.USER_TIER_LEVEL_ITEM.UPDATED_AT, item.updatedAt)
          .execute()
      }
    }
    return userTier
  }

  override fun findByIsPublicTrueOrderByCreatedAtDesc(): List<UserTier> {
    val records =
      dsl
        .selectFrom(UserTierTable.USER_TIER)
        .where(UserTierTable.USER_TIER.IS_PUBLIC.eq(true))
        .orderBy(UserTierTable.USER_TIER.CREATED_AT.desc())
        .fetch()

    // シンプルに user_tier のみを返すなら、ここで 1件ずつ reconstruct して返す
    return records.map { rec ->
      UserTier.reconstruct(
        id = UUID.fromString(rec[UserTierTable.USER_TIER.ID]),
        anonymousId = AnonymousId(rec[UserTierTable.USER_TIER.ANONYMOUS_ID]),
        categoryId = UUID.fromString(rec[UserTierTable.USER_TIER.CATEGORY_ID]),
        name = UserTierName(rec[UserTierTable.USER_TIER.NAME]),
        isPublic = rec[UserTierTable.USER_TIER.IS_PUBLIC],
        accessUrl = AccessUrl(rec[UserTierTable.USER_TIER.ACCESS_URL]),
        imagePath = rec[UserTierTable.USER_TIER.IMAGE_PATH],
        createdAt = rec[UserTierTable.USER_TIER.CREATED_AT],
        updatedAt = rec[UserTierTable.USER_TIER.UPDATED_AT],
        levels = emptyList() // Levelは取りに行っていないので空
      )
    }
  }

  override fun findLatest(limit: Int): List<UserTier> {
    val records =
      dsl
        .selectFrom(UserTierTable.USER_TIER)
        .orderBy(UserTierTable.USER_TIER.CREATED_AT.desc())
        .limit(limit)
        .fetch()

    return records.map { rec ->
      UserTier.reconstruct(
        id = UUID.fromString(rec[UserTierTable.USER_TIER.ID]),
        anonymousId = AnonymousId(rec[UserTierTable.USER_TIER.ANONYMOUS_ID]),
        categoryId = UUID.fromString(rec[UserTierTable.USER_TIER.CATEGORY_ID]),
        name = UserTierName(rec[UserTierTable.USER_TIER.NAME]),
        isPublic = rec[UserTierTable.USER_TIER.IS_PUBLIC],
        accessUrl = AccessUrl(rec[UserTierTable.USER_TIER.ACCESS_URL]),
        imagePath = rec[UserTierTable.USER_TIER.IMAGE_PATH],
        createdAt = rec[UserTierTable.USER_TIER.CREATED_AT],
        updatedAt = rec[UserTierTable.USER_TIER.UPDATED_AT],
        levels = emptyList()
      )
    }
  }

  override fun findSince(timestamp: Instant): List<UserTier> {
    val records =
      dsl
        .selectFrom(UserTierTable.USER_TIER)
        .where(UserTierTable.USER_TIER.CREATED_AT.gt(timestamp))
        .orderBy(UserTierTable.USER_TIER.CREATED_AT.desc())
        .fetch()

    return records.map { rec ->
      UserTier.reconstruct(
        id = UUID.fromString(rec[UserTierTable.USER_TIER.ID]),
        anonymousId = AnonymousId(rec[UserTierTable.USER_TIER.ANONYMOUS_ID]),
        categoryId = UUID.fromString(rec[UserTierTable.USER_TIER.CATEGORY_ID]),
        name = UserTierName(rec[UserTierTable.USER_TIER.NAME]),
        isPublic = rec[UserTierTable.USER_TIER.IS_PUBLIC],
        accessUrl = AccessUrl(rec[UserTierTable.USER_TIER.ACCESS_URL]),
        imagePath = rec[UserTierTable.USER_TIER.IMAGE_PATH],
        createdAt = rec[UserTierTable.USER_TIER.CREATED_AT],
        updatedAt = rec[UserTierTable.USER_TIER.UPDATED_AT],
        levels = emptyList()
      )
    }
  }
}
