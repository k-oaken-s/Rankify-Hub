package rankifyHub.tier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.UserTierRepository

@Service
class GetPublicTiersUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  fun getRecent(): List<TierWithCategory> {
    val userTiers = userTierRepository.findAllOrderByCreatedAtDesc().filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getRecentWithLimit(limit: Int): List<TierWithCategory> {
    val userTiers = userTierRepository.findLatest(limit).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }

  fun getCreatedAfter(timestamp: Instant): List<TierWithCategory> {
    val userTiers = userTierRepository.findSince(timestamp).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }
}
