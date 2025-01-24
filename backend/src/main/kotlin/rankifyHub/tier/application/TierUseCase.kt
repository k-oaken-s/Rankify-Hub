package rankifyHub.tier.application

import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.model.TierFactory
import rankifyHub.tier.domain.model.TierLevel
import rankifyHub.tier.domain.model.TierLevelItem
import rankifyHub.tier.domain.repository.TierRepository
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.OrderIndex
import rankifyHub.tier.domain.vo.TierName
import rankifyHub.tier.presentation.dto.CreateTierRequest

/** Tierの作成に関するユースケース */
@Service
class TierUseCase(
  private val tierRepository: TierRepository,
  private val tierFactory: TierFactory,
  private val fileStorageRepository: FileStorageRepository
) {

  /**
   * 新規Tierを作成
   *
   * @param request リクエスト
   * @return 作成されたTier
   */
  @Transactional
  fun create(request: CreateTierRequest): Tier {
    val anonymousId = AnonymousId(request.anonymousId)
    val categoryId = UUID.fromString(request.categoryId)
    val name = TierName(request.name)
    val isPublic = request.isPublic

    val levels =
      request.levels.map { levelRequest ->
        val level =
          TierLevel.create(
            tierId = UUID.randomUUID(),
            name = levelRequest.name,
            orderIndex = OrderIndex(levelRequest.orderIndex),
          )
        levelRequest.items.forEach { itemRequest ->
          val item =
            TierLevelItem.create(
              tierLevelId = level.id,
              tierId = UUID.randomUUID(), // 仮
              itemId = UUID.fromString(itemRequest.itemId),
              orderIndex = OrderIndex(itemRequest.orderIndex)
            )
          level.addItem(item)
        }
        level
      }

    val tier =
      tierFactory.create(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        levels = levels
      )

    return tierRepository.save(tier)
  }
}
