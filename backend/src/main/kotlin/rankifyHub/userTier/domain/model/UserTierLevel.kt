package rankifyHub.userTier.domain.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.OrderIndex

@Entity
@Table(
  name = "user_tier_level",
  uniqueConstraints = [UniqueConstraint(columnNames = ["user_tier_id", "order_index"])]
)
class UserTierLevel(
  @Id @Column(name = "id", nullable = false, updatable = false) val id: UUID = UUID.randomUUID(),
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_tier_id", nullable = false)
  var userTier: UserTier,
  @Column(name = "name", nullable = false) val name: String,
  @Embedded
  @AttributeOverride(name = "value", column = Column(name = "order_index", nullable = false))
  var orderIndex: OrderIndex = OrderIndex(1),
  @Column(name = "image_path", nullable = true) // 画像パスを追加
  var imagePath: String? = null,
  @Column(name = "created_at", nullable = false, updatable = false)
  val createdAt: Instant = Instant.now(),
  @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now(),
  @OneToMany(
    mappedBy = "userTierLevel",
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private var _items: MutableList<UserTierLevelItem> = mutableListOf()
) {

  val items: List<UserTierLevelItem>
    get() = _items.toList()

  /** アイテムを追加する */
  fun addItem(item: UserTierLevelItem) {
    val nextOrder = _items.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    item.orderIndex = OrderIndex(nextOrder)
    item.userTierLevel = this
    _items.add(item)
    refreshUpdatedAt()
  }

  /** アイテムを削除する */
  fun removeItem(item: UserTierLevelItem) {
    _items.remove(item)
    reorderItems()
    refreshUpdatedAt()
  }

  /** アイテムの順序を再整理する */
  private fun reorderItems() {
    _items.sortBy { it.orderIndex.value }
    _items.forEachIndexed { index, item -> item.updateOrder(OrderIndex(index + 1)) }
  }

  /** 並び順を更新する */
  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    refreshUpdatedAt()
  }

  /** 画像パスを更新する */
  fun updateImagePath(newImagePath: String?) {
    this.imagePath = newImagePath
    refreshUpdatedAt()
  }

  /** 更新日時をリフレッシュする */
  fun refreshUpdatedAt() {
    this.updatedAt = Instant.now()
  }
}
