package rankifyHub.tier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.OrderIndex

/** ティアレベル内の個別アイテムを表すドメインオブジェクト。 アイテムの順序と紐付け情報を管理する。 */
class UserTierLevelItem(
  val id: UUID = UUID.randomUUID(),
  var userTierLevelId: UUID,
  var userTierId: UUID,
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
      userTierLevelId: UUID,
      userTierId: UUID,
      itemId: UUID,
      orderIndex: OrderIndex,
    ): UserTierLevelItem {
      return UserTierLevelItem(
        id = UUID.randomUUID(),
        userTierLevelId = userTierLevelId,
        userTierId = userTierId,
        itemId = itemId,
        orderIndex = orderIndex,
      )
    }

    /** 新規アイテムを作成 */
    fun reconstruct(
      id: UUID,
      userTierLevelId: UUID,
      userTierId: UUID,
      itemId: UUID,
      orderIndex: OrderIndex,
      createdAt: Instant,
      updatedAt: Instant
    ): UserTierLevelItem {
      return UserTierLevelItem(
        id,
        userTierLevelId,
        userTierId,
        itemId,
        orderIndex,
        createdAt,
        updatedAt
      )
    }
  }
}
