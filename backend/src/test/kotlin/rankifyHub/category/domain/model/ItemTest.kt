package rankifyHub.category.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ItemTest :
  StringSpec({
    "名前と画像パスを指定して新しいアイテムを作成できること" {
      val name = "サンプルアイテム"
      val imagePath = "images/sample-item.jpg"

      val item = Item.create(name, imagePath, "サンプル説明")

      item.shouldBeInstanceOf<Item>()
      item.name shouldBe name
      item.imagePath shouldBe imagePath
      item.description shouldBe "サンプル説明"
      item.id shouldNotBe null
    }

    "画像パスがnullでも新しいアイテムを作成できること" {
      val name = "画像なしアイテム"

      val item = Item.create(name, null, null)

      item.name shouldBe name
      item.imagePath shouldBe null
      item.description shouldBe null
    }

    "アイテムの名前と画像パスを更新できること" {
      val originalName = "元のアイテム"
      val originalImagePath = "images/original-item.jpg"
      val item = Item.create(originalName, originalImagePath, "元の説明")

      val updatedName = "更新後のアイテム"
      val updatedImagePath = "images/updated-item.jpg"

      val updatedItem = item.update(updatedName, updatedImagePath, "更新後の説明")

      updatedItem.id shouldBe item.id
      updatedItem.name shouldBe updatedName
      updatedItem.imagePath shouldBe updatedImagePath
      updatedItem.description shouldBe "更新後の説明"
    }

    "画像パスをnullに更新できること" {
      val originalName = "元のアイテム"
      val originalImagePath = "images/original-item.jpg"
      val item = Item.create(originalName, originalImagePath, "元の説明")

      val updatedItem = item.update(originalName, null, "説明を変更")

      updatedItem.name shouldBe originalName
      updatedItem.imagePath shouldBe null
      updatedItem.description shouldBe "説明を変更"
    }

    "IDと名前のデフォルト値が設定されていること" {
      val item = Item.create("", null, null)

      item.id shouldNotBe null
      item.name shouldBe ""
    }
  })
