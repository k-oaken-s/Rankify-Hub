package rankifyHub.tier.application.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.tier.domain.model.UserTier

data class TierWithCategory(val userTier: UserTier, val category: Category)
