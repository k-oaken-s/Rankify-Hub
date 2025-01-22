package rankifyHub.category.domain.model

import java.util.*

/** カテゴリを表すドメインオブジェクト。 ランク付けの対象となるアイテムのグループを表現し、アイテムを管理する責務を持つ。 */
class Category
private constructor(
  val id: UUID,
  val name: String,
  val description: String?,
  val imagePath: String?,
  private val _items: MutableList<Item> = mutableListOf()
) {

  val items: List<Item>
    get() = _items.toList()

  companion object {

    /** 新規カテゴリを作成する。 一意なIDは自動的に生成される。 */
    fun create(name: String, description: String?, imagePath: String?): Category {
      return Category(
        id = UUID.randomUUID(),
        name = name,
        description = description,
        imagePath = imagePath
      )
    }

    /** カテゴリを再生成する。 このメソッドは主にインフラストラクチャ層での利用を想定している。 */
    fun reconstruct(
      id: UUID,
      name: String,
      description: String?,
      imagePath: String?,
      items: List<Item> = emptyList()
    ): Category {
      return Category(id, name, description, imagePath, items.toMutableList())
    }
  }

  /**
   * カテゴリに新しいアイテムを追加する。 追加されたアイテムは自動的に一意なIDが割り当てられる。
   *
   * @return 作成されたアイテム
   */
  fun addItem(name: String, imagePath: String?, description: String?): Item {
    val item = Item.create(name, imagePath, description)
    _items.add(item)
    return item
  }

  /**
   * 既存のアイテムを更新する。
   *
   * @param keepCurrentImage trueの場合、現在の画像パスを維持する
   * @return 更新されたアイテム
   * @throws IllegalArgumentException 指定されたIDのアイテムが存在しない場合
   */
  fun updateItem(
    itemId: UUID,
    name: String,
    imagePath: String?,
    keepCurrentImage: Boolean,
    description: String?
  ): Item {
    val targetIndex = _items.indexOfFirst { it.id == itemId }
    if (targetIndex == -1) throw IllegalArgumentException("Item not found")

    val current = _items[targetIndex]
    val updatedImagePath = if (keepCurrentImage) current.imagePath else imagePath

    val updated =
      current.update(name = name, imagePath = updatedImagePath, description = description)
    _items[targetIndex] = updated
    return updated
  }
}
