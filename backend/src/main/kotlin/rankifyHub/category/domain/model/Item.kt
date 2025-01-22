package rankifyHub.category.domain.model

import java.util.*

class Item
private constructor(
  val id: UUID,
  val name: String,
  val imagePath: String?,
  val description: String?,
) {

  companion object {
    fun create(name: String, imagePath: String?, description: String?): Item {
      return Item(
        id = UUID.randomUUID(),
        name = name,
        imagePath = imagePath,
        description = description,
      )
    }

    fun reconstruct(id: UUID, name: String, imagePath: String?, description: String?): Item {
      return Item(id, name, imagePath, description)
    }
  }

  fun update(name: String, imagePath: String?, description: String?): Item {
    return Item(id = this.id, name = name, imagePath = imagePath, description = description)
  }
}
