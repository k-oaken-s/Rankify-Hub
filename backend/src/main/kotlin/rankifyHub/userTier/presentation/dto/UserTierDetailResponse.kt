package rankifyHub.userTier.presentation.dto

import java.util.*
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.model.UserTierLevel
import rankifyHub.userTier.domain.model.UserTierLevelItem

data class UserTierDetailResponse(
  val id: UUID,
  val anonymousId: String,
  val categoryId: String,
  val name: String,
  val isPublic: Boolean,
  val accessUrl: String,
  val levels: List<UserTierLevelResponse>
) {
  companion object {
    fun fromEntity(userTier: UserTier): UserTierDetailResponse {
      return UserTierDetailResponse(
        id = userTier.id,
        anonymousId = userTier.anonymousId.value,
        categoryId = userTier.categoryId.toString(),
        name = userTier.name.value,
        isPublic = userTier.isPublic,
        accessUrl = userTier.accessUrl.value,
        levels = userTier.getLevels().map { UserTierLevelResponse.fromEntity(it) }
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
    fun fromEntity(userTierLevel: UserTierLevel): UserTierLevelResponse {
      return UserTierLevelResponse(
        id = userTierLevel.id,
        name = userTierLevel.name,
        order = userTierLevel.orderIndex.value,
        items = userTierLevel.items.map { UserTierItemResponse.fromEntity(it) }
      )
    }
  }
}

data class UserTierItemResponse(val itemId: UUID, val order: Int) {
  companion object {
    fun fromEntity(userTierItem: UserTierLevelItem): UserTierItemResponse {
      return UserTierItemResponse(itemId = userTierItem.id, order = userTierItem.orderIndex.value)
    }
  }
}
