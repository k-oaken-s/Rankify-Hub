package rankifyHub.userTier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.OrderIndex
import rankifyHub.userTier.domain.vo.UserTierName

/** jOOQ移行後はJPAアノテーションを外し、 純粋なドメインエンティティとする。 */
class UserTier(
  val id: UUID = UUID.randomUUID(),
  val anonymousId: AnonymousId,
  val categoryId: UUID,
  val name: UserTierName,
  val isPublic: Boolean = false,
  val accessUrl: AccessUrl,
  var imagePath: String? = null, // S3に保存された画像パス
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now(),
  private val levels: MutableList<UserTierLevel> = mutableListOf(),
) {

  /** Hibernateなどで使われていたデフォルトコンストラクタは不要になる。 */
  fun getLevels(): List<UserTierLevel> = levels.toList()

  fun addLevel(level: UserTierLevel) {
    // 順序を振り直す例
    val nextOrder = levels.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    level.orderIndex = OrderIndex(nextOrder)
    levels.add(level)
    refreshUpdatedAt()
  }

  fun removeLevel(level: UserTierLevel) {
    levels.remove(level)
    reorderLevels()
    refreshUpdatedAt()
  }

  private fun reorderLevels() {
    levels.sortBy { it.orderIndex.value }
    levels.forEachIndexed { index, lvl -> lvl.updateOrder(OrderIndex(index + 1)) }
  }

  fun updateImagePath(newImagePath: String?) {
    this.imagePath = newImagePath
    refreshUpdatedAt()
  }

  private fun refreshUpdatedAt() {
    this.updatedAt = Instant.now()
  }

  companion object {

    /** 新しくUserTierを作る場合（ファクトリメソッド）。 */
    fun create(
      anonymousId: AnonymousId,
      categoryId: UUID,
      name: UserTierName,
      isPublic: Boolean,
      imagePath: String?
    ): UserTier {
      return UserTier(
        id = UUID.randomUUID(),
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        accessUrl = AccessUrl(UUID.randomUUID().toString()),
        imagePath = imagePath
      )
    }

    /** DBから読み出すときなどに再構築する用途。 */
    fun reconstruct(
      id: UUID,
      anonymousId: AnonymousId,
      categoryId: UUID,
      name: UserTierName,
      isPublic: Boolean,
      accessUrl: AccessUrl,
      imagePath: String?,
      createdAt: Instant,
      updatedAt: Instant,
      levels: List<UserTierLevel>
    ): UserTier {
      return UserTier(
        id = id,
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        accessUrl = accessUrl,
        imagePath = imagePath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        levels = levels.toMutableList()
      )
    }
  }
}
