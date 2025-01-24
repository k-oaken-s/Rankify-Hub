package rankifyHub.tier.presentation.presenter

import org.springframework.stereotype.Component
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.presentation.dto.TierResponse

@Component
class TierPresenter(private val fileStorageRepository: FileStorageRepository) {

  /**
   * TierWithCategoryDtoをTierResponseに変換します。
   *
   * @param dto TierとCategory情報をまとめたDTO
   * @return クライアントに返却するレスポンスDTO
   */
  fun toResponse(dto: TierWithCategory): TierResponse {
    return TierResponse(
      id = dto.tier.id.toString(),
      accessUrl = dto.tier.accessUrl.value,
      createdAt = dto.tier.createdAt,
      name = dto.tier.name.value,
      categoryName = dto.category.name,
      categoryImageUrl = dto.category.imagePath ?: "",
      categoryId = dto.tier.categoryId.toString()
    )
  }
}
