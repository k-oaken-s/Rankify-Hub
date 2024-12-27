package rankifyHub.category.domain.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "item")
open class Item(
  @Id @GeneratedValue val id: UUID = UUID.randomUUID(),
  @Column(nullable = false) val name: String = "",
  val imagePath: String? = null,
  val description: String? = null,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  val category: Category? = null
) {

  companion object {
    fun create(name: String, imagePath: String?, category: Category, description: String?): Item {
      return Item(
        id = UUID.randomUUID(),
        name = name,
        imagePath = imagePath,
        description = description,
        category = category
      )
    }
  }

  fun update(name: String, imagePath: String?, description: String?): Item {
    return Item(
      id = this.id,
      name = name,
      imagePath = imagePath,
      description = description,
      category = this.category
    )
  }
}
