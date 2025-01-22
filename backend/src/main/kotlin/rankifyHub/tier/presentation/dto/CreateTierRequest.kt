package rankifyHub.tier.presentation.dto

data class CreateTierRequest(
    val anonymousId: String,
    val categoryId: String,
    val name: String,
    val isPublic: Boolean,
    val levels: List<TierLevelRequest>
)

data class TierLevelRequest(
    val name: String,
    val orderIndex: Int,
    val items: List<TierItemRequest>
)

data class TierItemRequest(val itemId: String, val orderIndex: Int)
