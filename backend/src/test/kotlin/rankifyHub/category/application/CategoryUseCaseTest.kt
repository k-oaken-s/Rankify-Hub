package rankifyHub.category.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.shared.domain.repository.FileStorageRepository

class CategoryUseCaseTest :
  StringSpec({
    val categoryRepository: CategoryRepository = mockk()
    val fileStorageRepository: FileStorageRepository = mockk()
    val categoryUseCase = CategoryUseCase(categoryRepository, fileStorageRepository)

    "すべてのカテゴリを取得できること" {
      val categories =
        listOf(
          Category.create("Category1", "Description1", null),
          Category.create("Category2", "Description2", null)
        )
      every { categoryRepository.findAll() } returns categories

      val result = categoryUseCase.getAllCategories()

      result shouldBe categories
      verify { categoryRepository.findAll() }
    }

    "指定されたIDのカテゴリを取得できること" {
      val categoryId = UUID.randomUUID()
      val category = Category.create("Category1", "Description1", null)
      every { categoryRepository.findById(categoryId) } returns category

      val result = categoryUseCase.getCategoryWithItems(categoryId)

      result shouldBe category
      verify { categoryRepository.findById(categoryId) }
    }

    "新しいカテゴリを追加できること" {
      val addCategoryDto = AddCategoryDto("New Category", "New Description")
      val category = Category.create(addCategoryDto.name, addCategoryDto.description, null)
      every { categoryRepository.save(any()) } returns category
      every { fileStorageRepository.saveFile(any(), any(), any(), any()) } returns "imagePath"

      val result = categoryUseCase.addCategory(addCategoryDto, ByteArray(0))

      result.name shouldBe addCategoryDto.name
      result.description shouldBe addCategoryDto.description
      verify {
        fileStorageRepository.saveFile("images", any(), any(), "jpg")
        categoryRepository.save(match { it.name == addCategoryDto.name })
      }
    }

    "カテゴリ内のアイテムを更新できること" {
      val categoryId = UUID.randomUUID()
      val itemId = UUID.randomUUID()
      val category = mockk<Category>(relaxed = true)
      val updatedItem = Item.create("Updated Item", "imagePath", description = "")

      every { categoryRepository.findById(categoryId) } returns category
      every {
        category.updateItem(
          itemId = itemId,
          name = "Updated Item",
          imagePath = "imagePath",
          keepCurrentImage = true,
          description = ""
        )
      } returns updatedItem
      every { categoryRepository.save(category) } returns category
      every { fileStorageRepository.saveFile(any(), any(), any(), any()) } returns "imagePath"

      val fileMock =
        mockk<org.springframework.web.multipart.MultipartFile> {
          every { bytes } returns ByteArray(0)
        }

      val json = """{"name":"Updated Item","description":""}"""

      val result = categoryUseCase.updateItemInCategory(categoryId, itemId, json, fileMock, true)

      result.name shouldBe "Updated Item"
    }

    "カテゴリが見つからない場合に例外をスローすること" {
      val categoryId = UUID.randomUUID()
      val itemId = UUID.randomUUID()
      every { categoryRepository.findById(categoryId) } returns null

      val exception =
        shouldThrow<IllegalArgumentException> {
          categoryUseCase.updateItemInCategory(
            categoryId,
            itemId,
            """{"name":"Updated Item"}""",
            null,
            true
          )
        }
      exception.message shouldBe "Category not found"

      verify { categoryRepository.findById(categoryId) }
    }
  })
