package rankifyHub.userTier.domain.model

import java.util.*
import org.springframework.stereotype.Component
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.UserTierName

@Component
class UserTierFactory {

  fun create(
    anonymousId: AnonymousId,
    categoryId: UUID,
    name: UserTierName,
    isPublic: Boolean,
    levels: List<UserTierLevel>,
    imagePath: String?
  ): UserTier {
    val userTier =
      UserTier.create(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        imagePath = imagePath
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
