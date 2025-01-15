package rankifyHub.userTier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.userTier.application.dto.UserTierWithCategory
import rankifyHub.userTier.domain.repository.UserTierRepository

@Service
class GetPublicUserTiersUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  fun getRecent(): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findAllOrderByCreatedAtDesc().filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getRecentWithLimit(limit: Int): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findLatest(limit).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getCreatedAfter(timestamp: Instant): List<UserTierWithCategory> {
    val userTiers = userTierRepository.findSince(timestamp).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { UserTierWithCategory(userTier = userTier, category = it) }
    }
  }
}
