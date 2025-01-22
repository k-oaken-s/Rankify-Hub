package rankifyHub.tier.presentation.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.model.UserTierLevel
import rankifyHub.tier.domain.model.UserTierLevelItem
import java.util.*

data class TierDetailResponse(
  val id: UUID,
  val anonymousId: String,
  val categoryId: String,
  val categoryName: String,
  val categoryImageUrl: String?,
  val name: String,
  val isPublic: Boolean,
  val accessUrl: String,
  val levels: List<TierLevelResponse>
) {
  companion object {
    fun fromEntity(userTier: UserTier, category: Category): TierDetailResponse {
      return TierDetailResponse(
        id = userTier.id,
        anonymousId = userTier.anonymousId.value,
        categoryId = userTier.categoryId.toString(),
        categoryName = category.name,
        categoryImageUrl = category.imagePath,
        name = userTier.name.value,
        isPublic = userTier.isPublic,
        accessUrl = userTier.accessUrl.value,
        levels = userTier.getLevels().map { TierLevelResponse.fromEntity(it, category) }
      )
    }
  }
}

data class TierLevelResponse(
  val id: UUID,
  val name: String,
  val order: Int,
  val items: List<TierItemResponse>
) {
  companion object {
    fun fromEntity(userTierLevel: UserTierLevel, category: Category): TierLevelResponse {
      return TierLevelResponse(
        id = userTierLevel.id,
        name = userTierLevel.name,
        order = userTierLevel.orderIndex.value,
        items =
          userTierLevel.items.map { tierItem ->
            val categoryItem =
              category.items.find { it.id == tierItem.itemId }
                ?: throw IllegalStateException("Item not found")
            TierItemResponse.fromEntity(tierItem, categoryItem)
          }
      )
    }
  }
}

data class TierItemResponse(
  val itemId: UUID,
  val order: Int,
  val name: String,
  val imageUrl: String?,
  val description: String?
) {
  companion object {
    fun fromEntity(userTierItem: UserTierLevelItem, categoryItem: Item): TierItemResponse {
      return TierItemResponse(
        itemId = userTierItem.id,
        order = userTierItem.orderIndex.value,
        name = categoryItem.name,
        imageUrl = categoryItem.imagePath,
        description = categoryItem.description
      )
    }
  }
}
