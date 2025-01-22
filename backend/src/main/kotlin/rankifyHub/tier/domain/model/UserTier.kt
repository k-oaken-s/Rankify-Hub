package rankifyHub.tier.domain.model

import rankifyHub.tier.domain.vo.AccessUrl
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.UserTierName
import java.time.Instant
import java.util.*

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

    fun getLevels(): List<UserTierLevel> = levels.toList()

    fun addLevel(level: UserTierLevel) {
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

    private fun refreshUpdatedAt() {
        this.updatedAt = Instant.now()
    }

    companion object {

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
