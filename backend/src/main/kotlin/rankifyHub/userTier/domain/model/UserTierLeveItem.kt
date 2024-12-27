package rankifyHub.userTier.domain.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.OrderIndex

@Entity
@Table(
  name = "user_tier_level_item",
  uniqueConstraints = [UniqueConstraint(columnNames = ["user_tier_level_id", "order_index"])]
)
class UserTierLevelItem(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO) // 自動生成戦略を明示
  var id: UUID = UUID.randomUUID(),
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_tier_level_id", nullable = false)
  var userTierLevel: UserTierLevel,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_tier_id", nullable = false)
  var userTier: UserTier,
  @Column(name = "item_id", nullable = false) var itemId: UUID,
  @Embedded
  @AttributeOverrides(
    AttributeOverride(name = "value", column = Column(name = "order_index", nullable = false))
  )
  var orderIndex: OrderIndex,
  @Column(name = "image_path", nullable = true) // 画像パスを追加
  var imagePath: String? = null,
  @Column(name = "created_at", nullable = false) var createdAt: Instant = Instant.now(),
  @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now()
) {

  // コンストラクタ（初期化用）
  constructor(
    userTierLevel: UserTierLevel,
    userTier: UserTier,
    itemId: UUID,
    orderIndex: OrderIndex,
    imagePath: String? = null // 画像パスを追加
  ) : this(
    id = UUID.randomUUID(),
    userTierLevel = userTierLevel,
    userTier = userTier,
    itemId = itemId,
    orderIndex = orderIndex,
    imagePath = imagePath,
    createdAt = Instant.now(),
    updatedAt = Instant.now()
  )

  /** 画像パスを更新 */
  fun updateImagePath(newImagePath: String?) {
    this.imagePath = newImagePath
    this.updatedAt = Instant.now()
  }

  /** 並び順を更新 */
  fun updateOrder(newOrder: OrderIndex) {
    this.orderIndex = newOrder
    this.updatedAt = Instant.now()
  }
}
