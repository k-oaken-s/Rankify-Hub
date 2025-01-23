package rankifyHub.tier.domain.repository

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.model.UserTier

/** Tierの永続化を担うリポジトリインターフェース。 Tier集約のルートでTierとLevel、TierItemの整合性を保証する。 */
interface UserTierRepository {
  fun save(userTier: UserTier): UserTier

  fun findById(userTierId: UUID): UserTier?

  fun findAllOrderByCreatedAtDesc(): List<UserTier>

  fun findLatest(limit: Int): List<UserTier>

  fun findSince(timestamp: Instant): List<UserTier>
}
