package rankifyHub.tier.application

import java.util.*
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.UserTierRepository

/** Tierの取得に関するユースケース */
@Service
class GetTierUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  /** 指定したIDのTierを取得 */
  fun getUserTierById(userTierId: UUID): TierWithCategory {
    val userTier =
      userTierRepository.findById(userTierId) ?: throw NoSuchElementException("UserTier not found")
    val category =
      categoryRepository.findById(userTier.categoryId)
        ?: throw NoSuchElementException("Category not found")

    return TierWithCategory(userTier, category)
  }
}
