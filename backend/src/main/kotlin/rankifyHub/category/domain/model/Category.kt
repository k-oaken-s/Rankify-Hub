package rankifyHub.category.domain.model

import java.util.*

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
    fun create(name: String, description: String?, imagePath: String?): Category {
      return Category(
        id = UUID.randomUUID(),
        name = name,
        description = description,
        imagePath = imagePath
      )
    }

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

  fun addItem(name: String, imagePath: String?, description: String?): Item {
    val item = Item.create(name, imagePath, description)
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
