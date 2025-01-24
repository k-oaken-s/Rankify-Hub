package rankifyHub.tier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.repository.TierRepository

/** 公開Tierを取得するユースケース */
@Service
class GetPublicTiersUseCase(
  private val tierRepository: TierRepository,
  private val categoryRepository: CategoryRepository
) {

  /** 新着Tierを全て取得 */
  fun getRecent(): List<TierWithCategory> {
    val tiers = tierRepository.findAllOrderByCreatedAtDesc().filter { it.isPublic }
    return tiers.mapNotNull { tier ->
      val category = categoryRepository.findById(tier.categoryId)
      category?.let { TierWithCategory(tier = tier, category = it) }
    }
  }

  /** 新着Tierを指定件数取得 */
  fun getRecentWithLimit(limit: Int): List<TierWithCategory> {
    val tiers = tierRepository.findLatest(limit).filter { it.isPublic }
    return tiers.mapNotNull { tier ->
      val category = categoryRepository.findById(tier.categoryId)
      category?.let { TierWithCategory(tier = tier, category = it) }
    }
  }

  /** 指定日時以降に作成されたTierを取得 */
  fun getCreatedAfter(timestamp: Instant): List<TierWithCategory> {
    val tiers = tierRepository.findSince(timestamp).filter { it.isPublic }
    return tiers.mapNotNull { tier ->
      val category = categoryRepository.findById(tier.categoryId)
      category?.let { TierWithCategory(tier = tier, category = it) }
    }
  }
}
