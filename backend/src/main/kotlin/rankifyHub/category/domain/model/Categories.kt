package rankifyHub.category.domain.model

import rankifyHub.shared.vo.SortOrder

/** カテゴリーのリストを表すファーストクラスコレクション カテゴリーの検索や並び替えなどのコレクション操作を担う。 */
class Categories private constructor(private val categories: List<Category>) {
  companion object {
    /** 既存のカテゴリーリストからCategoriesを生成する */
    fun from(categories: List<Category>): Categories = Categories(categories)
  }

  /**
   * カテゴリー名で部分一致検索を行う
   *
   * @param name 検索するカテゴリー名
   * @return フィルタリングされたCategories
   */
  fun filterByName(name: String): Categories =
    Categories(categories.filter { it.name.contains(name, ignoreCase = true) })

  /**
   * リリース日で並び替えを行う
   *
   * @param sortOrder 並び替えの順序（昇順/降順）
   * @return ソートされたCategories
   */
  fun sortByReleaseDate(sortOrder: SortOrder): Categories =
    Categories(
      when (sortOrder) {
        SortOrder.ASC -> categories.sortedBy { it.releaseDate }
        SortOrder.DESC -> categories.sortedByDescending { it.releaseDate }
      }
    )

  /** カテゴリーリストを取得する */
  fun toList(): List<Category> = categories
}
