package rankifyHub.userTier.presentation.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.model.UserTierLevel
import rankifyHub.userTier.domain.model.UserTierLevelItem
import java.util.*

data class UserTierDetailResponse(
    val id: UUID,
    val anonymousId: String,
    val categoryId: String,
    val categoryName: String, // 追加
    val categoryImageUrl: String?, // 追加
    val name: String,
    val isPublic: Boolean,
    val accessUrl: String,
    val levels: List<UserTierLevelResponse>
) {
    companion object {
        fun fromEntity(userTier: UserTier, category: Category): UserTierDetailResponse {
            return UserTierDetailResponse(
                id = userTier.id,
                anonymousId = userTier.anonymousId.value,
                categoryId = userTier.categoryId.toString(),
                categoryName = category.name,
                categoryImageUrl = category.imagePath,
                name = userTier.name.value,
                isPublic = userTier.isPublic,
                accessUrl = userTier.accessUrl.value,
                levels = userTier.getLevels().map { UserTierLevelResponse.fromEntity(it, category) }
            )
        }
    }
}

data class UserTierLevelResponse(
    val id: UUID,
    val name: String,
    val order: Int,
    val items: List<UserTierItemResponse>
) {
    companion object {
        fun fromEntity(userTierLevel: UserTierLevel, category: Category): UserTierLevelResponse {
            return UserTierLevelResponse(
                id = userTierLevel.id,
                name = userTierLevel.name,
                order = userTierLevel.orderIndex.value,
                items = userTierLevel.items.map { tierItem ->
                    val categoryItem = category.items.find { it.id == tierItem.itemId }
                        ?: throw IllegalStateException(
                            "Item not found"
                        )
                    UserTierItemResponse.fromEntity(tierItem, categoryItem)
                }
            )
        }
    }
}

data class UserTierItemResponse(
    val itemId: UUID,
    val order: Int,
    val name: String, // 追加
    val imageUrl: String?, // 追加
    val description: String? // 追加
) {
    companion object {
        fun fromEntity(userTierItem: UserTierLevelItem, categoryItem: Item): UserTierItemResponse {
            return UserTierItemResponse(
                itemId = userTierItem.id,
                order = userTierItem.orderIndex.value,
                name = categoryItem.name,
                imageUrl = categoryItem.imagePath,
                description = categoryItem.description
            )
        }
    }
}
