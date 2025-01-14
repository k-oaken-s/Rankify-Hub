package rankifyHub.userTier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.userTier.application.dto.UserTierWithCategory
import rankifyHub.userTier.domain.repository.UserTierRepository

@Service
class GetLatestUserTiersUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  fun getPublicUserTiers(): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findByIsPublicTrueOrderByCreatedAtDesc()
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getLatestUserTiers(limit: Int): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findLatest(limit)
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getUserTiersSince(timestamp: Instant): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findSince(timestamp)
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }
}
