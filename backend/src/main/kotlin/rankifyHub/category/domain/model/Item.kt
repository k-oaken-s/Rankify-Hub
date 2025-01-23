package rankifyHub.category.domain.model

import java.util.*

/** カテゴリ内の個別アイテムを表すドメインオブジェクト。 */
class Item
private constructor(
  val id: UUID,
  val name: String,
  val imagePath: String?,
  val description: String?,
) {

  /** 新規アイテムを作成する。 */
  companion object {
    fun create(name: String, imagePath: String?, description: String?): Item {
      return Item(
        id = UUID.randomUUID(),
        name = name,
        imagePath = imagePath,
        description = description,
      )
    }

    /** アイテムを再作成する。 このメソッドは主にインフラストラクチャ層での利用を想定している。 */
    fun reconstruct(id: UUID, name: String, imagePath: String?, description: String?): Item {
      return Item(id, name, imagePath, description)
    }
  }

  /**
   * アイテムの属性を更新する。 IDは維持される。
   *
   * @return 更新された新しいアイテムインスタンス
   */
  fun update(name: String, imagePath: String?, description: String?): Item {
    return Item(id = this.id, name = name, imagePath = imagePath, description = description)
  }
}
