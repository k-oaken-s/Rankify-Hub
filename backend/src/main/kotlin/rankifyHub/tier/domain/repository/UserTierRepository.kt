package rankifyHub.tier.domain.repository

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.model.UserTier

interface UserTierRepository {
  fun save(userTier: UserTier): UserTier

  fun findById(userTierId: UUID): UserTier?

  fun findAllOrderByCreatedAtDesc(): List<UserTier>

  fun findLatest(limit: Int): List<UserTier>

  fun findSince(timestamp: Instant): List<UserTier>
}
