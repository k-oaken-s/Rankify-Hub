package rankifyHub.userTier.application

import java.util.*
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.userTier.application.dto.UserTierWithCategory
import rankifyHub.userTier.domain.repository.UserTierRepository

@Service
class GetUserTierUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {
  fun getUserTierById(userTierId: UUID): UserTierWithCategory {
    val userTier =
      userTierRepository.findById(userTierId) ?: throw NoSuchElementException("UserTier not found")
    val category =
      categoryRepository.findById(userTier.categoryId)
        ?: throw NoSuchElementException("Category not found")

    return UserTierWithCategory(userTier, category)
  }
}
