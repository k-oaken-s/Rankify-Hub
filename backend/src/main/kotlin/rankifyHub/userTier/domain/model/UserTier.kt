package rankifyHub.userTier.domain.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.OrderIndex
import rankifyHub.userTier.domain.vo.UserTierName

/**
 * ユーザーが作成したTierを表すエンティティ
 *
 * @property id UUIDv7を使用した一意な識別子
 * @property anonymousId 匿名ユーザーの識別子
 * @property categoryId 紐づくカテゴリのID
 * @property name Tierの名前
 * @property isPublic Tierの公開設定
 * @property accessUrl アクセスURL（Tierの一意な公開用識別子）
 * @property imagePath S3に保存された画像へのパス（任意）
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
@Entity
@Table(name = "user_tier")
open class UserTier(
  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  val id: UUID = UUID.randomUUID(),
  @Embedded
  @AttributeOverride(name = "value", column = Column(name = "anonymous_id", nullable = false))
  val anonymousId: AnonymousId,
  @Column(name = "category_id", nullable = false) val categoryId: UUID,
  @Embedded
  @AttributeOverride(name = "value", column = Column(name = "name", nullable = false))
  val name: UserTierName,
  @Column(name = "is_public", nullable = false) val isPublic: Boolean = false,
  @Embedded
  @AttributeOverride(name = "value", column = Column(name = "access_url", nullable = false))
  val accessUrl: AccessUrl,
  @Column(name = "image_path", nullable = true) // 新規追加
  var imagePath: String? = null,
  @Column(name = "created_at", nullable = false, updatable = false)
  val createdAt: Instant = Instant.now(),
  @Column(name = "updated_at", nullable = false) var updatedAt: Instant = Instant.now(),
  @OneToMany(
    mappedBy = "userTier",
    cascade = [CascadeType.ALL],
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private var levels: MutableList<UserTierLevel> = mutableListOf(),
) {
  // Hibernate用のデフォルトコンストラクタ
  constructor() :
    this(
      UUID.randomUUID(),
      AnonymousId(),
      UUID.randomUUID(),
      UserTierName(),
      false,
      AccessUrl(),
      null, // imagePathの初期値
      Instant.now(),
      Instant.now(),
      mutableListOf()
    )

  fun getLevels(): MutableList<UserTierLevel> = levels

  fun addLevel(level: UserTierLevel) {
    val nextOrder = levels.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    level.orderIndex = OrderIndex(nextOrder)
    level.userTier = this
    levels.add(level)
    updateTimestamp()
  }

  fun removeLevel(level: UserTierLevel) {
    levels.remove(level)
    reorderLevels()
    updateTimestamp()
  }

  private fun reorderLevels() {
    levels.sortBy { it.orderIndex.value }
    levels.forEachIndexed { index, level -> level.updateOrder(OrderIndex(index + 1)) }
  }

  private fun updateTimestamp() {
    updatedAt = Instant.now()
  }

  /**
   * 画像パスを更新します。
   *
   * @param newImagePath 新しい画像パス
   */
  fun updateImagePath(newImagePath: String?) {
    this.imagePath = newImagePath
    updateTimestamp()
  }
}
