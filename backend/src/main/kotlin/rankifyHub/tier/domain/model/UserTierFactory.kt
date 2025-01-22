package rankifyHub.tier.domain.model

import org.springframework.stereotype.Component
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.UserTierName
import java.util.*

@Component
class UserTierFactory {

    fun create(
        anonymousId: AnonymousId,
        categoryId: UUID,
        name: UserTierName,
        isPublic: Boolean,
        levels: List<UserTierLevel>
    ): UserTier {
        val userTier =
            UserTier.create(
                anonymousId = anonymousId,
                categoryId = categoryId,
                name = name,
                isPublic = isPublic
            )

        // UserTierLevel の userTierId をセットし、items も紐付け
        levels.forEach { level ->
            level.userTierId = userTier.id
            userTier.addLevel(level)
            level.items.forEach { item ->
                item.userTierId = userTier.id
                item.userTierLevelId = level.id
            }
        }
        return userTier
    }
}
