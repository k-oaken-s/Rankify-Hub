package rankifyHub.tier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.OrderIndex

/** Tier Level内の個別アイテムを表すドメインオブジェクト。 アイテムの順序と紐付け情報を管理する。 */
class TierLevelItem(
  val id: UUID = UUID.randomUUID(),
  var tierLevelId: UUID,
  var tierId: UUID,
  var itemId: UUID,
  var orderIndex: OrderIndex,
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now()
) {

  /** アイテムの順序を更新 */
  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    this.updatedAt = Instant.now()
  }

  companion object {
    /** 新規アイテムを作成 */
    fun create(
      tierLevelId: UUID,
      tierId: UUID,
      itemId: UUID,
      orderIndex: OrderIndex,
    ): TierLevelItem {
      return TierLevelItem(
        id = UUID.randomUUID(),
        tierLevelId = tierLevelId,
        tierId = tierId,
        itemId = itemId,
        orderIndex = orderIndex,
      )
    }

    /** 新規アイテムを作成 */
    fun reconstruct(
      id: UUID,
      tierLevelId: UUID,
      tierId: UUID,
      itemId: UUID,
      orderIndex: OrderIndex,
      createdAt: Instant,
      updatedAt: Instant
    ): TierLevelItem {
      return TierLevelItem(id, tierLevelId, tierId, itemId, orderIndex, createdAt, updatedAt)
    }
  }
}
