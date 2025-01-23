package rankifyHub.category.domain.repository

import java.util.*
import rankifyHub.category.domain.model.Category

/** カテゴリーリポジトリインターフェース。 カテゴリーアイテム集約ルート */
interface CategoryRepository {
  /** 全カテゴリを取得する */
  fun findAll(): List<Category>

  /** 指定されたIDのカテゴリを取得する */
  fun findById(id: UUID): Category?

  /** カテゴリを保存する。 新規作成時はIDが生成され、更新時は既存のIDが維持される。 */
  fun save(category: Category): Category

  /** 指定されたIDのカテゴリを削除する */
  fun deleteById(id: UUID)
}
