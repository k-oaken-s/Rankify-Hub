package rankifyHub.category.domain.vo

import rankifyHub.shared.vo.SortOrder

/** カテゴリーの検索条件を表す値オブジェクト */
data class CategorySearchCriteria(
  val sortOrder: SortOrder = SortOrder.DESC,
  val nameFilter: String? = null
)
