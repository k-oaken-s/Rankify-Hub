package rankifyHub.category.application

import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.shared.domain.repository.FileStorageRepository

@Service
class CategoryUseCase(
  private val categoryRepository: CategoryRepository,
  private val fileStorageRepository: FileStorageRepository
) {

  fun getAllCategories(): List<Category> = categoryRepository.findAll()

  fun getCategoryWithItems(id: UUID): Category =
    categoryRepository.findById(id).orElseThrow { IllegalArgumentException("Category not found") }

  fun addCategory(addCategoryDto: AddCategoryDto, fileBytes: ByteArray?): Category {
    val imagePath =
      fileBytes?.let {
        val tmpId = UUID.randomUUID().toString()
        fileStorageRepository.saveFile("images", tmpId, it, "jpg")
      }

    val category =
      Category.create(
        name = addCategoryDto.name,
        description = addCategoryDto.description,
        imagePath = imagePath
      )
    return categoryRepository.save(category)
  }

  fun deleteCategory(id: UUID) = categoryRepository.deleteById(id)

  @Transactional
  fun addItemToCategory(categoryId: UUID, itemJson: String, fileBytes: ByteArray?): Item {
    val category =
      categoryRepository.findById(categoryId).orElseThrow {
        IllegalArgumentException("Category not found")
      }

    val jsonNode = ObjectMapper().readTree(itemJson)
    val itemName = jsonNode.get("name").asText()
    val description = jsonNode.path("description").asText(null)

    val imagePath =
      fileBytes?.let {
        fileStorageRepository.saveFile(
          "images",
          categoryId.toString() + "-" + UUID.randomUUID().toString(),
          it,
          "jpg"
        )
      }

    val newItem =
      category.addItem(name = itemName, imagePath = imagePath, description = description)
    categoryRepository.save(category)
    return newItem
  }

  @Transactional
  fun updateItemInCategory(
    categoryId: UUID,
    itemId: UUID,
    itemJson: String,
    file: MultipartFile?,
    keepCurrentImage: Boolean
  ): Item {
    val category =
      categoryRepository.findById(categoryId).orElseThrow {
        IllegalArgumentException("Category not found")
      }
    val jsonNode = ObjectMapper().readTree(itemJson)
    val itemName = jsonNode.get("name").asText()
    val description = jsonNode.path("description").asText(null)

    val imagePath =
      file?.bytes?.let {
        fileStorageRepository.saveFile(
          "images",
          categoryId.toString() + "-" + itemId.toString(),
          it,
          "jpg"
        )
      }

    val updatedItem =
      category.updateItem(
        itemId = itemId,
        name = itemName,
        imagePath = imagePath,
        keepCurrentImage = keepCurrentImage,
        description = description
      )

    categoryRepository.save(category)
    return updatedItem
  }
}
