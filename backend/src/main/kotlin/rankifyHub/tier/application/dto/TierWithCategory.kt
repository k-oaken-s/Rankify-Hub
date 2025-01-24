package rankifyHub.tier.application.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.tier.domain.model.Tier

data class TierWithCategory(val tier: Tier, val category: Category)
