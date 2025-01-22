package rankifyHub.tier.domain.model

import rankifyHub.tier.domain.vo.OrderIndex
import java.time.Instant
import java.util.*

class UserTierLevel(
  val id: UUID = UUID.randomUUID(),
  var userTierId: UUID,
  val name: String,
  var orderIndex: OrderIndex = OrderIndex(1),
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now(),
  private val _items: MutableList<UserTierLevelItem> = mutableListOf()
) {

  val items: List<UserTierLevelItem>
    get() = _items.toList()

  fun addItem(item: UserTierLevelItem) {
    val nextOrder = _items.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    item.orderIndex = OrderIndex(nextOrder)
    _items.add(item)
    refreshUpdatedAt()
  }

  fun removeItem(item: UserTierLevelItem) {
    _items.remove(item)
    reorderItems()
    refreshUpdatedAt()
  }

  private fun reorderItems() {
    _items.sortBy { it.orderIndex.value }
    _items.forEachIndexed { index, it -> it.updateOrder(OrderIndex(index + 1)) }
  }

  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    refreshUpdatedAt()
  }

  private fun refreshUpdatedAt() {
    this.updatedAt = Instant.now()
  }

  companion object {
    fun create(
      userTierId: UUID,
      name: String,
      orderIndex: OrderIndex,
    ): UserTierLevel {
      return UserTierLevel(
        id = UUID.randomUUID(),
        userTierId = userTierId,
        name = name,
        orderIndex = orderIndex,
      )
    }

    fun reconstruct(
      id: UUID,
      userTierId: UUID,
      name: String,
      orderIndex: OrderIndex,
      createdAt: Instant,
      updatedAt: Instant,
      items: List<UserTierLevelItem>
    ): UserTierLevel {
      return UserTierLevel(
        id = id,
        userTierId = userTierId,
        name = name,
        orderIndex = orderIndex,
        createdAt = createdAt,
        updatedAt = updatedAt,
        _items = items.toMutableList()
      )
    }
  }
}
