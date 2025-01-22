package rankifyHub.tier.application

import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.UserTierRepository
import java.util.*

@Service
class GetTierUseCase(
    private val userTierRepository: UserTierRepository,
    private val categoryRepository: CategoryRepository
) {
    fun getUserTierById(userTierId: UUID): TierWithCategory {
        val userTier =
            userTierRepository.findById(userTierId) ?: throw NoSuchElementException("UserTier not found")
        val category =
            categoryRepository.findById(userTier.categoryId)
                ?: throw NoSuchElementException("Category not found")

        return TierWithCategory(userTier, category)
    }
}
