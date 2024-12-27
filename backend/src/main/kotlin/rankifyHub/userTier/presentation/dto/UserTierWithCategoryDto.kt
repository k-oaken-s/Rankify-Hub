package rankifyHub.userTier.presentation.dto

import rankifyHub.userTier.domain.model.UserTier

/** UserTierとCategory情報をまとめたDTO */
data class UserTierWithCategoryDto(
  val userTier: UserTier,
  val categoryName: String,
  val categoryImagePath: String? // S3などのパスを保持
)
