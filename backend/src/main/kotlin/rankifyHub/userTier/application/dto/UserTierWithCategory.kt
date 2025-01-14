package rankifyHub.userTier.application.dto

import rankifyHub.category.domain.model.Category
import rankifyHub.userTier.domain.model.UserTier

data class UserTierWithCategory(val userTier: UserTier, val category: Category)
