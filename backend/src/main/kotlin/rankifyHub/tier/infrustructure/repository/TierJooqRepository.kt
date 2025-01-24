package rankifyHub.tier.infrustructure.repository

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.tables.Tier as JTier
import rankifyHub.tables.TierLevel as JTierLevel
import rankifyHub.tables.TierLevelItem as JTierLevelItem
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.model.TierLevel
import rankifyHub.tier.domain.model.TierLevelItem
import rankifyHub.tier.domain.repository.TierRepository
import rankifyHub.tier.domain.vo.AccessUrl
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.TierName

/** TierのJOOQ永続化実装 */
@Repository
class TierJooqRepository(private val dsl: DSLContext) : TierRepository {

  /** Tierとその関連エンティティを保存 */
  override fun save(tier: Tier): Tier {
    dsl
      .insertInto(JTier.TIER)
      .set(JTier.TIER.ID, tier.id)
      .set(JTier.TIER.ANONYMOUS_ID, tier.anonymousId.value)
      .set(JTier.TIER.CATEGORY_ID, tier.categoryId)
      .set(JTier.TIER.NAME, tier.name.value)
      .set(JTier.TIER.IS_PUBLIC, tier.isPublic)
      .set(JTier.TIER.ACCESS_URL, tier.accessUrl.value)
      .set(
        JTier.TIER.CREATED_AT,
        tier.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
      )
      .set(
        JTier.TIER.UPDATED_AT,
        tier.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
      )
      .onDuplicateKeyUpdate()
      .set(JTier.TIER.NAME, tier.name.value)
      .set(JTier.TIER.IS_PUBLIC, tier.isPublic)
      .set(JTier.TIER.ACCESS_URL, tier.accessUrl.value)
      .set(JTier.TIER.UPDATED_AT, LocalDateTime.now())
      .execute()

    dsl
      .deleteFrom(JTierLevelItem.TIER_LEVEL_ITEM)
      .where(JTierLevelItem.TIER_LEVEL_ITEM.TIER_ID.eq(tier.id))
      .execute()

    dsl.deleteFrom(JTierLevel.TIER_LEVEL).where(JTierLevel.TIER_LEVEL.TIER_ID.eq(tier.id)).execute()

    tier.getLevels().forEach { level ->
      dsl
        .insertInto(JTierLevel.TIER_LEVEL)
        .set(JTierLevel.TIER_LEVEL.ID, level.id)
        .set(JTierLevel.TIER_LEVEL.TIER_ID, tier.id)
        .set(JTierLevel.TIER_LEVEL.NAME, level.name)
        .set(JTierLevel.TIER_LEVEL.ORDER_INDEX, level.orderIndex.value)
        .set(
          JTierLevel.TIER_LEVEL.CREATED_AT,
          level.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
        )
        .set(
          JTierLevel.TIER_LEVEL.UPDATED_AT,
          level.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) } ?: LocalDateTime.now()
        )
        .execute()

      level.items.forEach { item ->
        dsl
          .insertInto(JTierLevelItem.TIER_LEVEL_ITEM)
          .set(JTierLevelItem.TIER_LEVEL_ITEM.ID, item.id)
          .set(JTierLevelItem.TIER_LEVEL_ITEM.TIER_LEVEL_ID, level.id)
          .set(JTierLevelItem.TIER_LEVEL_ITEM.TIER_ID, tier.id)
          .set(JTierLevelItem.TIER_LEVEL_ITEM.ITEM_ID, item.itemId)
          .set(JTierLevelItem.TIER_LEVEL_ITEM.ORDER_INDEX, item.orderIndex.value)
          .set(
            JTierLevelItem.TIER_LEVEL_ITEM.CREATED_AT,
            item.createdAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
              ?: LocalDateTime.now()
          )
          .set(
            JTierLevelItem.TIER_LEVEL_ITEM.UPDATED_AT,
            item.updatedAt.let { LocalDateTime.ofInstant(it, ZoneOffset.UTC) }
              ?: LocalDateTime.now()
          )
          .execute()
      }
    }
    return tier
  }

  /** 指定IDのTierと関連エンティティを取得 */
  override fun findById(tierId: UUID): Tier? {
    val tierRecord =
      dsl.selectFrom(JTier.TIER).where(JTier.TIER.ID.eq(tierId)).fetchOne() ?: return null

    val levelRecords =
      dsl
        .selectFrom(JTierLevel.TIER_LEVEL)
        .where(JTierLevel.TIER_LEVEL.TIER_ID.eq(tierId))
        .orderBy(JTierLevel.TIER_LEVEL.ORDER_INDEX.asc())
        .fetch()

    val levels =
      levelRecords.map { levelRec ->
        val levelId = levelRec[JTierLevel.TIER_LEVEL.ID]

        val itemRecords =
          dsl
            .selectFrom(JTierLevelItem.TIER_LEVEL_ITEM)
            .where(JTierLevelItem.TIER_LEVEL_ITEM.TIER_LEVEL_ID.eq(levelId))
            .orderBy(JTierLevelItem.TIER_LEVEL_ITEM.ORDER_INDEX.asc())
            .fetch()

        val levelItems =
          itemRecords.map { itemRec ->
            TierLevelItem.reconstruct(
              id = itemRec[JTierLevelItem.TIER_LEVEL_ITEM.ID],
              tierLevelId = levelId,
              tierId = tierId,
              itemId = itemRec[JTierLevelItem.TIER_LEVEL_ITEM.ITEM_ID],
              orderIndex = OrderIndex(itemRec[JTierLevelItem.TIER_LEVEL_ITEM.ORDER_INDEX]),
              createdAt =
                itemRec[JTierLevelItem.TIER_LEVEL_ITEM.CREATED_AT]?.toInstant(ZoneOffset.UTC)
                  ?: Instant.now(),
              updatedAt =
                itemRec[JTierLevelItem.TIER_LEVEL_ITEM.UPDATED_AT]?.toInstant(ZoneOffset.UTC)
                  ?: Instant.now()
            )
          }

        TierLevel.reconstruct(
          id = levelId,
          tierId = tierId,
          name = levelRec[JTierLevel.TIER_LEVEL.NAME],
          orderIndex = OrderIndex(levelRec[JTierLevel.TIER_LEVEL.ORDER_INDEX]),
          createdAt = levelRec[JTierLevel.TIER_LEVEL.CREATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          updatedAt = levelRec[JTierLevel.TIER_LEVEL.UPDATED_AT]?.toInstant(ZoneOffset.UTC)
              ?: Instant.now(),
          items = levelItems
        )
      }

    return Tier.reconstruct(
      id = tierRecord[JTier.TIER.ID],
      anonymousId = AnonymousId(tierRecord[JTier.TIER.ANONYMOUS_ID]),
      categoryId = tierRecord[JTier.TIER.CATEGORY_ID],
      name = TierName(tierRecord[JTier.TIER.NAME]),
      isPublic = tierRecord[JTier.TIER.IS_PUBLIC],
      accessUrl = AccessUrl(tierRecord[JTier.TIER.ACCESS_URL]),
      createdAt = tierRecord[JTier.TIER.CREATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
      updatedAt = tierRecord[JTier.TIER.UPDATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
      levels = levels
    )
  }

  /** 全てのTierを作成日時の降順で取得 */
  override fun findAllOrderByCreatedAtDesc(): List<Tier> {
    val records = dsl.selectFrom(JTier.TIER).orderBy(JTier.TIER.CREATED_AT.desc()).fetch()

    return records.map { rec ->
      Tier.reconstruct(
        id = rec.get(JTier.TIER.ID),
        anonymousId = AnonymousId(rec.get(JTier.TIER.ANONYMOUS_ID)),
        categoryId = rec.get(JTier.TIER.CATEGORY_ID),
        name = TierName(rec.get(JTier.TIER.NAME)),
        isPublic = rec.get(JTier.TIER.IS_PUBLIC),
        accessUrl = AccessUrl(rec.get(JTier.TIER.ACCESS_URL)),
        createdAt = rec.get(JTier.TIER.CREATED_AT)?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        updatedAt = rec.get(JTier.TIER.UPDATED_AT)?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        levels = emptyList()
      )
    }
  }

  /** 作成日時が最新のTierを指定件数取得 */
  override fun findLatest(limit: Int): List<Tier> {
    val records =
      dsl.selectFrom(JTier.TIER).orderBy(JTier.TIER.CREATED_AT.desc()).limit(limit).fetch()

    return records.map { rec ->
      Tier.reconstruct(
        id = rec[JTier.TIER.ID],
        anonymousId = AnonymousId(rec[JTier.TIER.ANONYMOUS_ID]),
        categoryId = rec[JTier.TIER.CATEGORY_ID],
        name = TierName(rec[JTier.TIER.NAME]),
        isPublic = rec[JTier.TIER.IS_PUBLIC],
        accessUrl = AccessUrl(rec[JTier.TIER.ACCESS_URL]),
        createdAt = rec[JTier.TIER.CREATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        updatedAt = rec[JTier.TIER.UPDATED_AT]?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        levels = emptyList()
      )
    }
  }

  /** 指定日時以降に作成されたTierを取得する */
  override fun findSince(timestamp: Instant): List<Tier> {
    val records =
      dsl
        .selectFrom(JTier.TIER)
        .where(
          JTier.TIER.CREATED_AT.greaterOrEqual(LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC))
        )
        .orderBy(JTier.TIER.CREATED_AT.desc())
        .fetch()

    return records.map { rec ->
      Tier.reconstruct(
        id = rec.get(JTier.TIER.ID),
        anonymousId = AnonymousId(rec.get(JTier.TIER.ANONYMOUS_ID)),
        categoryId = rec.get(JTier.TIER.CATEGORY_ID),
        name = TierName(rec.get(JTier.TIER.NAME)),
        isPublic = rec.get(JTier.TIER.IS_PUBLIC),
        accessUrl = AccessUrl(rec.get(JTier.TIER.ACCESS_URL)),
        createdAt = rec.get(JTier.TIER.CREATED_AT).toInstant(ZoneOffset.UTC) ?: Instant.now(),
        updatedAt = rec.get(JTier.TIER.UPDATED_AT)?.toInstant(ZoneOffset.UTC) ?: Instant.now(),
        levels = emptyList()
      )
    }
  }
}
