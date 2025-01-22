package rankifyHub.tier.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.model.UserTierFactory
import rankifyHub.tier.domain.model.UserTierLevel
import rankifyHub.tier.domain.model.UserTierLevelItem
import rankifyHub.tier.domain.repository.UserTierRepository
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.UserTierName
import rankifyHub.tier.presentation.dto.CreateTierRequest
import java.util.*

@Service
class TierUseCase(
    private val userTierRepository: UserTierRepository,
    private val userTierFactory: UserTierFactory,
    private val fileStorageRepository: FileStorageRepository
) {

    @Transactional
    fun create(request: CreateTierRequest): UserTier {
        val anonymousId = AnonymousId(request.anonymousId)
        val categoryId = UUID.fromString(request.categoryId)
        val name = UserTierName(request.name)
        val isPublic = request.isPublic

        val levels =
            request.levels.map { levelRequest ->
                val level =
                    UserTierLevel.create(
                        userTierId = UUID.randomUUID(), // 仮
                        name = levelRequest.name,
                        orderIndex = OrderIndex(levelRequest.orderIndex),
                    )
                levelRequest.items.forEach { itemRequest ->
                    val item =
                        UserTierLevelItem.create(
                            userTierLevelId = level.id,
                            userTierId = UUID.randomUUID(), // 仮
                            itemId = UUID.fromString(itemRequest.itemId),
                            orderIndex = OrderIndex(itemRequest.orderIndex)
                        )
                    level.addItem(item)
                }
                level
            }

        val userTier =
            userTierFactory.create(
                anonymousId = anonymousId,
                categoryId = categoryId,
                name = name,
                isPublic = isPublic,
                levels = levels
            )

        return userTierRepository.save(userTier)
    }
}
