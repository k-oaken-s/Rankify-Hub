package rankifyHub.category.presentation.controller

import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import rankifyHub.category.application.AddCategoryDto
import rankifyHub.category.application.CategoryUseCase
import rankifyHub.category.domain.vo.CategorySearchCriteria
import rankifyHub.category.presentation.dto.CategoryResponse
import rankifyHub.category.presentation.dto.ItemResponse
import rankifyHub.shared.vo.SortOrder

/** カテゴリに関するREST APIエンドポイントを提供するコントローラ。 */
@RestController
@RequestMapping("/categories")
class CategoryController(private val categoryUseCase: CategoryUseCase) {

  /** カテゴリ一覧を取得 */
  @GetMapping
  fun getAllCategories(
    @RequestParam(required = false) name: String?,
    @RequestParam(defaultValue = "DESC") sortOrder: SortOrder
  ): ResponseEntity<List<CategoryResponse>> {
    val criteria = CategorySearchCriteria(sortOrder = sortOrder, nameFilter = name)
    val categories = categoryUseCase.searchCategories(criteria)

    val response =
      categories.map { category ->
        CategoryResponse(
          id = category.id,
          name = category.name,
          description = category.description,
          image = category.imagePath,
          releaseDate = category.releaseDate
        )
      }
    return ResponseEntity.ok(response)
  }

  /**
   * 指定IDのカテゴリとそのアイテム一覧を取得
   *
   * @throws IllegalArgumentException カテゴリが存在しない場合
   */
  @GetMapping("/{categoryId}")
  fun getCategoryWithItems(@PathVariable categoryId: UUID): ResponseEntity<CategoryResponse> {
    val category = categoryUseCase.getCategoryWithItems(categoryId)
    val response =
      CategoryResponse(
        id = category.id,
        name = category.name,
        description = category.description,
        image = category.imagePath,
        releaseDate = category.releaseDate,
        items =
          category.items.map { item ->
            ItemResponse(id = item.id, name = item.name, image = item.imagePath)
          }
      )
    return ResponseEntity.ok(response)
  }

  /**
   * 新規カテゴリを作成
   *
   * @param file カテゴリ画像（オプション）
   */
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
        releaseDate = category.releaseDate,
        image = category.imagePath
      )
    return ResponseEntity.ok(response)
  }

  /**
   * カテゴリにアイテムを追加
   *
   * @param itemJson アイテム情報のJSON
   * @param file アイテム画像（オプション）
   */
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

  /**
   * カテゴリ内のアイテムを更新
   *
   * @param keepCurrentImage 現在の画像を保持するか（デフォルト: false）
   */
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
