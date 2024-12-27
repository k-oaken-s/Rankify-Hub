package rankifyHub.category.infrastructure.repository

import java.util.*
import org.springframework.stereotype.Repository
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.repository.CategoryRepository

/** Categoryリポジトリのインフラ層実装 */
@Repository
class CategoryRepositoryImpl(private val categoryJpaRepository: CategoryJpaRepository) :
  CategoryRepository {

  override fun findAll(): List<Category> = categoryJpaRepository.findAll()

  override fun findById(id: UUID): Optional<Category> =
    categoryJpaRepository.findById(id.toString())

  override fun save(category: Category): Category = categoryJpaRepository.save(category)

  override fun deleteById(id: UUID) = categoryJpaRepository.deleteById(id.toString())
}
