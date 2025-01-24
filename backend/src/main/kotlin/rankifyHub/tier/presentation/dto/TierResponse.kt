package rankifyHub.tier.presentation.dto

import java.time.Instant

/** クライアントに返却するTierのレスポンスDTO */
data class TierResponse(
  val id: String,
  val accessUrl: String,
  val createdAt: Instant,
  val name: String,
  val categoryName: String,
  val categoryImageUrl: String,
  val categoryId: String
)
