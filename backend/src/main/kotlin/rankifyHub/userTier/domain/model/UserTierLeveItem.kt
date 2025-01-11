package rankifyHub.userTier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.OrderIndex

class UserTierLevelItem(
  val id: UUID = UUID.randomUUID(),
  var userTierLevelId: UUID,
  var userTierId: UUID,
  var itemId: UUID,
  var orderIndex: OrderIndex,
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now()
) {

  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    this.updatedAt = Instant.now()
  }

  companion object {
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
