package rankifyHub.category.application

import java.time.LocalDate

data class AddCategoryDto(val name: String, val releaseDate: LocalDate, val description: String?)
