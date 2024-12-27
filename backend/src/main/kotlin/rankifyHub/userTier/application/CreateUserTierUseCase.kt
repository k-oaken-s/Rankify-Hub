package rankifyHub.userTier.application

import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.model.UserTierFactory
import rankifyHub.userTier.domain.model.UserTierLevel
import rankifyHub.userTier.domain.model.UserTierLevelItem
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.OrderIndex
import rankifyHub.userTier.domain.vo.UserTierName
import rankifyHub.userTier.presentation.dto.CreateUserTierRequest

@Service
class CreateUserTierUseCase(
  private val userTierRepository: UserTierRepository,
  private val userTierFactory: UserTierFactory,
  private val fileStorageRepository: FileStorageRepository
) {

  @Transactional
  fun create(request: CreateUserTierRequest, imageFile: MultipartFile?): UserTier {
    val anonymousId = AnonymousId(request.anonymousId)
    val categoryId = UUID.fromString(request.categoryId)
    val name = UserTierName(request.name)
    val isPublic = request.isPublic

    val imagePath =
      imageFile?.bytes?.let {
        val uniqueId = "${anonymousId.value}-${System.currentTimeMillis()}"
        fileStorageRepository.saveFile("user-tier-images", uniqueId, it, "jpg")
      }

    // UserTier を先に作成・保存して ID を付与
    val userTier =
      UserTier(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        accessUrl = AccessUrl(UUID.randomUUID().toString()),
        imagePath = imagePath
      )
    userTierRepository.save(userTier)

    // レベルデータとアイテムを作成
    request.levels.forEach { levelRequest ->
      val level =
        UserTierLevel(
          userTier = userTier,
          name = levelRequest.name,
          orderIndex = OrderIndex(levelRequest.orderIndex)
        )
      levelRequest.items.forEach { itemRequest ->
        val item =
          UserTierLevelItem(
            userTierLevel = level,
            userTier = userTier,
            itemId = UUID.fromString(itemRequest.itemId),
            orderIndex = OrderIndex(itemRequest.orderIndex)
          )
        level.addItem(item)
      }
      userTier.addLevel(level)
    }

    // UserTier を再度保存してレベルとアイテムを永続化
    return userTierRepository.save(userTier)
  }
}
