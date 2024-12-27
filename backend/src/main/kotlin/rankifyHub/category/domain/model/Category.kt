package rankifyHub.category.domain.model

import jakarta.persistence.*
import java.util.*

/** カテゴリーエンティティ。 */
@Entity
@Table(name = "category")
open class Category(
  @Id
  @GeneratedValue
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  val id: UUID = UUID.randomUUID(),
  @Column(nullable = false) val name: String = "",
  val description: String? = null,

  /** 画像のバイナリを保持していた代わりに、 「オブジェクトストレージ上のキー or URL」を文字列として保持。 */
  val imagePath: String? = null,
  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "category_id")
  private val _items: MutableList<Item> = mutableListOf()
) {

  val items: List<Item>
    get() = _items.toList()

  override fun toString(): String {
    return "Category(id=$id, name='$name', description=$description)"
  }

  companion object {
    fun create(name: String, description: String?, imagePath: String?): Category {
      return Category(
        id = UUID.randomUUID(),
        name = name,
        description = description,
        imagePath = imagePath
      )
    }
  }

  fun addItem(name: String, imagePath: String?, description: String?): Item {
    val item =
      Item.create(name = name, imagePath = imagePath, category = this, description = description)
    _items.add(item)
    return item
  }

  fun updateItem(
    itemId: UUID,
    name: String,
    imagePath: String?,
    keepCurrentImage: Boolean,
    description: String?
  ): Item {
    val item = _items.find { it.id == itemId } ?: throw IllegalArgumentException("Item not found")

    val updatedImagePath = if (keepCurrentImage) item.imagePath else imagePath

    val updatedItem =
      item.update(name = name, imagePath = updatedImagePath, description = description)

    _items.removeIf { it.id == itemId }
    _items.add(updatedItem)
    return updatedItem
  }
}
