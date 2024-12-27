package rankifyHub.category.presentation.controller

import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import rankifyHub.category.application.AddCategoryDto
import rankifyHub.category.application.CategoryUseCase
import rankifyHub.category.presentation.dto.CategoryResponse
import rankifyHub.category.presentation.dto.ItemResponse

/** RESTコントローラー: カテゴリおよびカテゴリ内のアイテムを管理します。 */
@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryUseCase: CategoryUseCase) {

  /** 全てのカテゴリを取得 */
  @GetMapping
  fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
    val categories = categoryUseCase.getAllCategories()

    val response =
      categories.map { category ->
        CategoryResponse(
          id = category.id,
          name = category.name,
          description = category.description,
          image = category.imagePath
        )
      }
    return ResponseEntity.ok(response)
  }

  /** カテゴリ1件とそのアイテム一覧を取得 */
  @GetMapping("/{categoryId}")
  fun getCategoryWithItems(@PathVariable categoryId: UUID): ResponseEntity<CategoryResponse> {
    val category = categoryUseCase.getCategoryWithItems(categoryId)
    val response =
      CategoryResponse(
        id = category.id,
        name = category.name,
        description = category.description,
        image = category.imagePath,
        items =
          category.items.map { item ->
            ItemResponse(id = item.id, name = item.name, image = item.imagePath)
          }
      )
    return ResponseEntity.ok(response)
  }

  /** 新しいカテゴリを追加 */
  @PostMapping
  fun addCategory(
    @RequestPart("category") categoryDto: AddCategoryDto,
    @RequestPart("file", required = false) file: MultipartFile?
  ): ResponseEntity<CategoryResponse> {
    val category = categoryUseCase.addCategory(categoryDto, file?.bytes)
    val response =
      CategoryResponse(
        id = category.id,
        name = category.name,
        description = category.description,
        image = category.imagePath
      )
    return ResponseEntity.ok(response)
  }

  /** カテゴリにアイテムを追加 */
  @PostMapping("/{categoryId}/items")
  fun addItemToCategory(
    @PathVariable categoryId: UUID,
    @RequestPart("item") itemJson: String,
    @RequestPart(value = "file", required = false) file: MultipartFile?
  ): ResponseEntity<ItemResponse> {
    val item = categoryUseCase.addItemToCategory(categoryId, itemJson, file?.bytes)
    val response = ItemResponse(id = item.id, name = item.name, image = item.imagePath)
    return ResponseEntity.ok(response)
  }

  /** カテゴリ内のアイテムを更新 */
  @PutMapping("/{categoryId}/items/{itemId}")
  fun updateItemInCategory(
    @PathVariable categoryId: UUID,
    @PathVariable itemId: UUID,
    @RequestPart("item") itemJson: String,
    @RequestPart(value = "file", required = false) file: MultipartFile?,
    @RequestParam(value = "keepCurrentImage", defaultValue = "false") keepCurrentImage: Boolean
  ): ResponseEntity<ItemResponse> {
    val updatedItem =
      categoryUseCase.updateItemInCategory(categoryId, itemId, itemJson, file, keepCurrentImage)
    val response =
      ItemResponse(id = updatedItem.id, name = updatedItem.name, image = updatedItem.imagePath)
    return ResponseEntity.ok(response)
  }
}
