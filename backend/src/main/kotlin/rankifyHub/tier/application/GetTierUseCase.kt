package rankifyHub.tier.application

import java.util.*
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.TierRepository

/** Tierの取得に関するユースケース */
@Service
class GetTierUseCase(
  private val tierRepository: TierRepository,
  private val categoryRepository: CategoryRepository
) {

  /** 指定したIDのTierを取得 */
  fun getTierById(tierId: UUID): TierWithCategory {
    val tier = tierRepository.findById(tierId) ?: throw NoSuchElementException("Tier not found")
    val category =
      categoryRepository.findById(tier.categoryId)
        ?: throw NoSuchElementException("Category not found")

    return TierWithCategory(tier, category)
  }
}
