package rankifyHub.tier.domain.model

import java.util.*
import org.springframework.stereotype.Component
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.TierName

@Component
class TierFactory {

  fun create(
    anonymousId: AnonymousId,
    categoryId: UUID,
    name: TierName,
    isPublic: Boolean,
    levels: List<TierLevel>
  ): Tier {
    val tier =
      Tier.create(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic
      )

    levels.forEach { level ->
      level.tierId = tier.id
      tier.addLevel(level)
      level.items.forEach { item ->
        item.tierId = tier.id
        item.tierLevelId = level.id
      }
    }
    return tier
  }
}
