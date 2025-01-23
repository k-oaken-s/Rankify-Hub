package rankifyHub.tier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.OrderIndex

/** ティア内の個別レベルを表すドメインオブジェクト。 レベル内のアイテムの順序管理と更新履歴を担う。 */
class UserTierLevel(
  val id: UUID = UUID.randomUUID(),
  var userTierId: UUID,
  val name: String,
  var orderIndex: OrderIndex = OrderIndex(1),
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now(),
  private val _items: MutableList<UserTierLevelItem> = mutableListOf()
) {

  /** アイテム一覧を取得 */
  val items: List<UserTierLevelItem>
    get() = _items.toList()

  /** アイテムを最後尾に追加する */
  fun addItem(item: UserTierLevelItem) {
    val nextOrder = _items.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    item.orderIndex = OrderIndex(nextOrder)
    _items.add(item)
    refreshUpdatedAt()
  }

  /** アイテムを削除し、残りのアイテムの順序を再整列 */
  fun removeItem(item: UserTierLevelItem) {
    _items.remove(item)
    reorderItems()
    refreshUpdatedAt()
  }

  /** アイテムの順序を1から連番で振り直す */
  private fun reorderItems() {
    _items.sortBy { it.orderIndex.value }
    _items.forEachIndexed { index, it -> it.updateOrder(OrderIndex(index + 1)) }
  }

  /** レベルの順序を更新 */
  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    refreshUpdatedAt()
  }

  private fun refreshUpdatedAt() {
    this.updatedAt = Instant.now()
  }

  companion object {
    /** 新規レベルを作成 */
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

    /** レベルを再作成 */
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
