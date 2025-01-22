package rankifyHub.category.presentation.controller

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import rankifyHub.category.application.AddCategoryDto
import rankifyHub.category.application.CategoryUseCase
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item

class CategoryControllerTest :
  DescribeSpec({
    lateinit var categoryUseCase: CategoryUseCase
    lateinit var controller: CategoryController

    beforeTest {
      categoryUseCase = mockk()
      controller = CategoryController(categoryUseCase)
    }

    describe("getAllCategories") {
      it("should return list of categories") {
        val category1 =
          mockk<Category> {
            every { id } returns UUID.randomUUID()
            every { name } returns "Category 1"
            every { description } returns "Description 1"
            every { imagePath } returns "image1.jpg"
          }
        val category2 =
          mockk<Category> {
            every { id } returns UUID.randomUUID()
            every { name } returns "Category 2"
            every { description } returns "Description 2"
            every { imagePath } returns null
          }

        every { categoryUseCase.getAllCategories() } returns listOf(category1, category2)

        val response = controller.getAllCategories()

        response.statusCode shouldBe HttpStatus.OK
        response.body?.size shouldBe 2
        response.body?.get(0)?.name shouldBe "Category 1"
        response.body?.get(1)?.name shouldBe "Category 2"

        verify { categoryUseCase.getAllCategories() }
      }
    }

    describe("getCategoryWithItems") {
      it("should return category with items") {
        val categoryId = UUID.randomUUID()
        val item1 =
          mockk<Item> {
            every { id } returns UUID.randomUUID()
            every { name } returns "Item 1"
            every { imagePath } returns "item1.jpg"
          }
        val item2 =
          mockk<Item> {
            every { id } returns UUID.randomUUID()
            every { name } returns "Item 2"
            every { imagePath } returns null
          }

        val category =
          mockk<Category> {
            every { id } returns categoryId
            every { name } returns "Test Category"
            every { description } returns "Test Description"
            every { imagePath } returns "category.jpg"
            every { items } returns listOf(item1, item2)
          }

        every { categoryUseCase.getCategoryWithItems(categoryId) } returns category

        val response = controller.getCategoryWithItems(categoryId)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "Test Category"
        response.body?.items?.size shouldBe 2
        response.body?.items?.get(0)?.name shouldBe "Item 1"

        verify { categoryUseCase.getCategoryWithItems(categoryId) }
      }
    }

    describe("addCategory") {
      it("should add new category") {
        val categoryDto = AddCategoryDto("New Category", "New Description")
        val file =
          MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".toByteArray())

        val newCategory =
          mockk<Category> {
            every { id } returns UUID.randomUUID()
            every { name } returns categoryDto.name
            every { description } returns categoryDto.description
            every { imagePath } returns "new-category.jpg"
          }

        every { categoryUseCase.addCategory(categoryDto, file.bytes) } returns newCategory

        val response = controller.addCategory(categoryDto, file)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "New Category"
        response.body?.description shouldBe "New Description"

        verify { categoryUseCase.addCategory(categoryDto, file.bytes) }
      }

      it("should add category without image") {
        val categoryDto = AddCategoryDto("New Category", "New Description")

        val newCategory =
          mockk<Category> {
            every { id } returns UUID.randomUUID()
            every { name } returns categoryDto.name
            every { description } returns categoryDto.description
            every { imagePath } returns null
          }

        every { categoryUseCase.addCategory(categoryDto, null) } returns newCategory

        val response = controller.addCategory(categoryDto, null)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "New Category"
        response.body?.image shouldBe null

        verify { categoryUseCase.addCategory(categoryDto, null) }
      }
    }

    describe("addItemToCategory") {
      it("should add item to category") {
        val categoryId = UUID.randomUUID()
        val itemJson = """{"name": "New Item"}"""
        val file =
          MockMultipartFile("file", "item.jpg", "image/jpeg", "test image content".toByteArray())

        val newItem =
          mockk<Item> {
            every { id } returns UUID.randomUUID()
            every { name } returns "New Item"
            every { imagePath } returns "new-item.jpg"
          }

        every { categoryUseCase.addItemToCategory(categoryId, itemJson, file.bytes) } returns
          newItem

        val response = controller.addItemToCategory(categoryId, itemJson, file)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "New Item"

        verify { categoryUseCase.addItemToCategory(categoryId, itemJson, file.bytes) }
      }
    }

    describe("updateItemInCategory") {
      it("should update item in category") {
        val categoryId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val itemJson = """{"name": "Updated Item"}"""
        val file =
          MockMultipartFile(
            "file",
            "updated.jpg",
            "image/jpeg",
            "updated image content".toByteArray()
          )

        val updatedItem =
          mockk<Item> {
            every { id } returns itemId
            every { name } returns "Updated Item"
            every { imagePath } returns "updated-item.jpg"
          }

        every {
          categoryUseCase.updateItemInCategory(categoryId, itemId, itemJson, file, false)
        } returns updatedItem

        val response = controller.updateItemInCategory(categoryId, itemId, itemJson, file, false)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "Updated Item"

        verify { categoryUseCase.updateItemInCategory(categoryId, itemId, itemJson, file, false) }
      }

      it("should update item without changing image") {
        val categoryId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val itemJson = """{"name": "Updated Item"}"""

        val updatedItem =
          mockk<Item> {
            every { id } returns itemId
            every { name } returns "Updated Item"
            every { imagePath } returns "existing-image.jpg"
          }

        every {
          categoryUseCase.updateItemInCategory(categoryId, itemId, itemJson, null, true)
        } returns updatedItem

        val response = controller.updateItemInCategory(categoryId, itemId, itemJson, null, true)

        response.statusCode shouldBe HttpStatus.OK
        response.body?.name shouldBe "Updated Item"
        response.body?.image shouldBe "existing-image.jpg"

        verify { categoryUseCase.updateItemInCategory(categoryId, itemId, itemJson, null, true) }
      }
    }
  })
