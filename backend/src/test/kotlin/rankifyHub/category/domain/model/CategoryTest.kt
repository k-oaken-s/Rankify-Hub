package rankifyHub.category.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.util.*

class CategoryTest :
  StringSpec({
    "カテゴリーが正しく作成されること" {
      val name = "Test Category"
      val description = "Test Description"
      val imagePath = "images/test-category.jpg"
      val releaseDate = LocalDate.of(2025, 1, 1)

      val category = Category.create(name, description, imagePath, releaseDate)

      category.name shouldBe name
      category.description shouldBe description
      category.imagePath shouldBe imagePath
    }

    "カテゴリーにアイテムを追加できること" {
      val category =
        Category.create("Test Category", "Test Description", null, LocalDate.of(2025, 1, 1))
      val itemName = "Test Item"
      val imagePath = "images/test-item.jpg"

      val item =
        category.addItem(
          name = itemName,
          imagePath = imagePath,
          description = "Test Item Description"
        )

      item.name shouldBe itemName
      item.imagePath shouldBe imagePath
      category.items shouldBe listOf(item)
    }

    "カテゴリー内のアイテムを更新できること" {
      val category =
        Category.create("Test Category", "Test Description", null, LocalDate.of(2025, 1, 1))
      val item =
        category.addItem(name = "Old Item", imagePath = null, description = "Old Description")

      val updatedItem =
        category.updateItem(
          itemId = item.id,
          name = "Updated Item",
          imagePath = "images/updated-item.jpg",
          keepCurrentImage = false,
          description = "Updated Description"
        )

      updatedItem.name shouldBe "Updated Item"
      updatedItem.imagePath shouldBe "images/updated-item.jpg"
      updatedItem.description shouldBe "Updated Description"
    }

    "存在しないアイテムを更新しようとすると例外がスローされること" {
      val category = Category.create("テストカテゴリー", null, null, LocalDate.of(2025, 1, 1))

      shouldThrow<IllegalArgumentException> {
          category.updateItem(
            itemId = UUID.randomUUID(),
            name = "更新済みアイテム",
            imagePath = null,
            keepCurrentImage = true,
            description = null
          )
        }
        .message shouldBe "Item not found"
    }

    "アイテム更新時にkeepCurrentImage=trueの場合、既存の画像が保持されること" {
      val category = Category.create("テストカテゴリー", null, null, LocalDate.of(2025, 1, 1))
      val item = category.addItem("アイテム名", "初期画像のパス", "説明")

      val updatedItem =
        category.updateItem(
          itemId = item.id,
          name = "更新済みアイテム",
          imagePath = null,
          keepCurrentImage = true,
          description = "更新済み説明"
        )

      updatedItem.name shouldBe "更新済みアイテム"
      updatedItem.imagePath shouldBe "初期画像のパス"
      updatedItem.description shouldBe "更新済み説明"
    }
  })
