package rankifyHub.userTier.presentation.dto

import java.time.Instant

/** クライアントに返却するUserTierのレスポンスDTO */
data class UserTierResponse(
  val id: String,
  val accessUrl: String,
  val createdAt: Instant,
  val name: String,
  val categoryName: String,
  val categoryImageUrl: String,
  val categoryId: String
)
