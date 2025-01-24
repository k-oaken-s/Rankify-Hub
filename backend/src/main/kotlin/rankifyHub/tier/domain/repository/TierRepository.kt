package rankifyHub.tier.domain.repository

import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.model.Tier

/** Tierの永続化を担うリポジトリインターフェース。 Tier集約のルートでTierとLevel、TierItemの整合性を保証する。 */
interface TierRepository {
  fun save(tier: Tier): Tier

  fun findById(tierId: UUID): Tier?

  fun findAllOrderByCreatedAtDesc(): List<Tier>

  fun findLatest(limit: Int): List<Tier>

  fun findSince(timestamp: Instant): List<Tier>
}
