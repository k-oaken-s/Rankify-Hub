package rankifyHub.category.domain.repository

import java.util.*
import rankifyHub.category.domain.model.Category

interface CategoryRepository {
  fun findAll(): List<Category>

  fun findById(id: UUID): Optional<Category>

  fun save(category: Category): Category

  fun deleteById(id: UUID)
}
