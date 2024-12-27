package rankifyHub.category.presentation.dto

import java.util.*

data class CategoryResponse(
  val id: UUID,
  val name: String,
  val description: String?,
  val image: String? = null,
  val items: List<ItemResponse> = emptyList()
)
