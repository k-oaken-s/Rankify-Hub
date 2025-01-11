package rankifyHub.userTier.domain.repository

import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.model.UserTier

interface UserTierRepository {
  fun save(userTier: UserTier): UserTier

  fun findById(userTierId: UUID): UserTier?

  fun findByIsPublicTrueOrderByCreatedAtDesc(): List<UserTier>

  fun findLatest(limit: Int): List<UserTier>

  fun findSince(timestamp: Instant): List<UserTier>
}
