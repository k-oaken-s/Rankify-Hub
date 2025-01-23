package rankifyHub.tier.domain.model

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.AccessUrl
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.UserTierName

/** ユーザーが作成したアイテムの階層構造を表すドメインオブジェクト。 複数のレベルとレベルごとの複数のアイテムを持つ。 */
class UserTier(
  val id: UUID = UUID.randomUUID(),
  val anonymousId: AnonymousId,
  val categoryId: UUID,
  val name: UserTierName,
  val isPublic: Boolean = false,
  val accessUrl: AccessUrl,
  val createdAt: Instant = Instant.now(),
  var updatedAt: Instant = Instant.now(),
  private val levels: MutableList<UserTierLevel> = mutableListOf(),
) {

  /** レベルの一覧を取得 */
  fun getLevels(): List<UserTierLevel> = levels.toList()

  /** レベルを追加する。 追加時に自動的に最後尾の順序が割り当てられる。 */
  fun addLevel(level: UserTierLevel) {
    val nextOrder = levels.maxOfOrNull { it.orderIndex.value }?.plus(1) ?: 1
    level.orderIndex = OrderIndex(nextOrder)
    levels.add(level)
    refreshUpdatedAt()
  }

  /** レベルを削除し、残りのレベルの順序を再整列する。 */
  fun removeLevel(level: UserTierLevel) {
    levels.remove(level)
    reorderLevels()
    refreshUpdatedAt()
  }

  /** レベルの順序を1から連番で振り直す。 現在の順序を維持したまま隙間を埋める。 */
  private fun reorderLevels() {
    levels.sortBy { it.orderIndex.value }
    levels.forEachIndexed { index, lvl -> lvl.updateOrder(OrderIndex(index + 1)) }
  }

  private fun refreshUpdatedAt() {
    this.updatedAt = Instant.now()
  }

  companion object {

    /** 新規Tierを作成する。 */
    fun create(
      anonymousId: AnonymousId,
      categoryId: UUID,
      name: UserTierName,
      isPublic: Boolean,
    ): UserTier {
      return UserTier(
        id = UUID.randomUUID(),
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        accessUrl = AccessUrl(UUID.randomUUID().toString()),
      )
    }

    /** Tierを再作成する。 */
    fun reconstruct(
      id: UUID,
      anonymousId: AnonymousId,
      categoryId: UUID,
      name: UserTierName,
      isPublic: Boolean,
      accessUrl: AccessUrl,
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
        createdAt = createdAt,
        updatedAt = updatedAt,
        levels = levels.toMutableList()
      )
    }
  }
}
