package rankifyHub.userTier.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.model.UserTierFactory
import rankifyHub.userTier.domain.model.UserTierLevel
import rankifyHub.userTier.domain.model.UserTierLevelItem
import rankifyHub.userTier.domain.repository.UserTierRepository
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.OrderIndex
import rankifyHub.userTier.domain.vo.UserTierName
import rankifyHub.userTier.presentation.dto.CreateUserTierRequest
import java.util.*

@Service
open class CreateUserTierUseCase(
  private val userTierRepository: UserTierRepository,
  private val userTierFactory: UserTierFactory,
  private val fileStorageRepository: FileStorageRepository
) {

  @Transactional
  open fun create(request: CreateUserTierRequest, imageFile: MultipartFile?): UserTier {
    val anonymousId = AnonymousId(request.anonymousId)
    val categoryId = UUID.fromString(request.categoryId)
    val name = UserTierName(request.name)
    val isPublic = request.isPublic

    val imagePath =
      imageFile?.bytes?.let {
        val uniqueId = "${anonymousId.value}-${System.currentTimeMillis()}"
        fileStorageRepository.saveFile("user-tier-images", uniqueId, it, "jpg")
      }

    // levelsを組み立て
    val levels =
      request.levels.map { levelRequest ->
        val level =
          UserTierLevel.create(
            userTierId = UUID.randomUUID(), // 仮
            name = levelRequest.name,
            orderIndex = OrderIndex(levelRequest.orderIndex),
            imagePath = null
          )
        levelRequest.items.forEach { itemRequest ->
          val item =
            UserTierLevelItem.create(
              userTierLevelId = level.id,
              userTierId = UUID.randomUUID(), // 仮
              itemId = UUID.fromString(itemRequest.itemId),
              orderIndex = OrderIndex(itemRequest.orderIndex)
            )
          level.addItem(item)
        }
        level
      }

    // ドメイン側ファクトリで生成
    val userTier =
      userTierFactory.create(
        anonymousId = anonymousId,
        categoryId = categoryId,
        name = name,
        isPublic = isPublic,
        levels = levels
      )

    // jOOQベースのリポジトリで保存
    return userTierRepository.save(userTier)
  }
}
