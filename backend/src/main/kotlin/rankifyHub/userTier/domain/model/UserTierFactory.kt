package rankifyHub.userTier.domain.model

import java.util.*
import org.springframework.stereotype.Component
import rankifyHub.userTier.domain.vo.AccessUrl
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
      UserTier(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        accessUrl = AccessUrl(UUID.randomUUID().toString()),
        imagePath = imagePath
      )

    // レベルデータとアイテムを紐付け
    levels
      .sortedBy { it.orderIndex.value }
      .forEach { level ->
        level.userTier = userTier
        userTier.addLevel(level)
        level.items
          .sortedBy { it.orderIndex.value }
          .forEach { item ->
            item.userTier = userTier
            item.userTierLevel = level
          }
      }

    return userTier
  }
}
