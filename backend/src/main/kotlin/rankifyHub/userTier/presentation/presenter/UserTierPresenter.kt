package rankifyHub.userTier.presentation.presenter

import org.springframework.stereotype.Component
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.userTier.presentation.dto.UserTierResponse
import rankifyHub.userTier.presentation.dto.UserTierWithCategoryDto

@Component
class UserTierPresenter(private val fileStorageRepository: FileStorageRepository) {

  /**
   * UserTierWithCategoryDtoをUserTierResponseに変換します。
   *
   * @param dto UserTierとCategory情報をまとめたDTO
   * @return クライアントに返却するレスポンスDTO
   */
  fun toResponse(dto: UserTierWithCategoryDto): UserTierResponse {
    val categoryImageUrl =
      dto.categoryImagePath?.let { fileStorageRepository.generateUrl(it) } ?: ""
    return UserTierResponse(
      accessUrl = dto.userTier.accessUrl.value,
      createdAt = dto.userTier.createdAt,
      name = dto.userTier.name.value,
      categoryName = dto.categoryName,
      categoryImageUrl = categoryImageUrl
    )
  }
}
