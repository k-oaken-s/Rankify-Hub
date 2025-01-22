package rankifyHub.tier.domain.model

import rankifyHub.tier.domain.vo.OrderIndex
import java.time.Instant
import java.util.*

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
