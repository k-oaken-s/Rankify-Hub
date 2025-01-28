package rankifyHub.tier.presentation.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.model.TierLevel
import rankifyHub.tier.domain.model.TierLevelItem
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
    fun fromEntity(tier: Tier, category: Category): TierDetailResponse {
      return TierDetailResponse(
        id = tier.id,
        anonymousId = tier.anonymousId.value,
        categoryId = tier.categoryId.toString(),
        categoryName = category.name,
        categoryImageUrl = category.imagePath,
        name = tier.name.value,
        isPublic = tier.isPublic,
        accessUrl = tier.accessUrl.value,
        levels = tier.getLevels().map { TierLevelResponse.fromEntity(it, category) }
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
    fun fromEntity(tierLevel: TierLevel, category: Category): TierLevelResponse {
      return TierLevelResponse(
        id = tierLevel.id,
        name = tierLevel.name,
        order = tierLevel.orderIndex.value,
        items =
          tierLevel.items.map { tierItem ->
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
  val id: UUID,
  val itemId: UUID,
  val order: Int,
  val name: String,
  val imageUrl: String?,
  val description: String?
) {
  companion object {
    fun fromEntity(tierItem: TierLevelItem, categoryItem: Item): TierItemResponse {
      return TierItemResponse(
        id = tierItem.id,
        itemId = tierItem.itemId,
        order = tierItem.orderIndex.value,
        name = categoryItem.name,
        imageUrl = categoryItem.imagePath,
        description = categoryItem.description
      )
    }
  }
}
