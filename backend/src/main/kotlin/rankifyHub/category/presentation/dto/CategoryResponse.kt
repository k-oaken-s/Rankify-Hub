package rankifyHub.category.presentation.dto

import java.time.LocalDate
import java.util.*

data class CategoryResponse(
  val id: UUID,
  val name: String,
  val description: String?,
  val image: String? = null,
  val releaseDate: LocalDate,
  val items: List<ItemResponse> = emptyList()
)
