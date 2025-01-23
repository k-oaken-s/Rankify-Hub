package rankifyHub.tier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.UserTierRepository

/** 公開Tierを取得するユースケース */
@Service
class GetPublicTiersUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  /** 新着Tierを全て取得 */
  fun getRecent(): List<TierWithCategory> {
    val userTiers = userTierRepository.findAllOrderByCreatedAtDesc().filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }

  /** 新着Tierを指定件数取得 */
  fun getRecentWithLimit(limit: Int): List<TierWithCategory> {
    val userTiers = userTierRepository.findLatest(limit).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }

  /** 指定日時以降に作成されたTierを取得 */
  fun getCreatedAfter(timestamp: Instant): List<TierWithCategory> {
    val userTiers = userTierRepository.findSince(timestamp).filter { it.isPublic }
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId)
      category?.let { TierWithCategory(userTier = userTier, category = it) }
    }
  }
}
