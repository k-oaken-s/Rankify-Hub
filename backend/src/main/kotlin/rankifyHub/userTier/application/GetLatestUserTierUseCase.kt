package rankifyHub.userTier.application

import java.time.Instant
import org.springframework.stereotype.Service
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.presentation.dto.UserTierWithCategoryDto

@Service
class GetLatestUserTiersUseCase(
  private val userTierRepository: UserTierRepository,
  private val categoryRepository: CategoryRepository
) {

  fun getPublicUserTiers(): List<UserTierWithCategoryDto> {
    val userTiers = userTierRepository.findByIsPublicTrueOrderByCreatedAtDesc()
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId).orElse(null)
      category?.let {
        UserTierWithCategoryDto(
          userTier = userTier,
          categoryName = it.name,
          categoryImagePath = it.imagePath // 画像パスを使用
        )
      }
    }
  }

  fun getLatestUserTiers(limit: Int): List<UserTierWithCategoryDto> {
    val userTiers = userTierRepository.findLatest(limit)
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId).orElse(null)
      category?.let {
        UserTierWithCategoryDto(
          userTier = userTier,
          categoryName = it.name,
          categoryImagePath = it.imagePath // 画像パスを使用
        )
      }
    }
  }

  fun getUserTiersSince(timestamp: Instant): List<UserTierWithCategoryDto> {
    val userTiers = userTierRepository.findSince(timestamp)
    return userTiers.mapNotNull { userTier ->
      val category = categoryRepository.findById(userTier.categoryId).orElse(null)
      category?.let {
        UserTierWithCategoryDto(
          userTier = userTier,
          categoryName = it.name,
          categoryImagePath = it.imagePath // 画像パスを使用
        )
      }
    }
  }
}
