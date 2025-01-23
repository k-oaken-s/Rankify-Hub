package rankifyHub.category.domain.repository

import rankifyHub.category.domain.model.Item

/** アイテムリポジトリインターフェース。 カテゴリ集約の一部 */
interface ItemRepository {
  /**
   * 最新のアイテムを指定件数取得する
   *
   * @param limit 取得する最大件数
   */
  fun findLatest(limit: Int): List<Item>

  /**
   * カスタム条件でアイテムを検索する
   *
   * @param condition 検索条件文字列
   */
  fun findByCustomCondition(condition: String): List<Item>
}
