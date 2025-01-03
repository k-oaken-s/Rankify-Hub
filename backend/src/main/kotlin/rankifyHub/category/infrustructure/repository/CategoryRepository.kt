package rankifyHub.category.infrastructure.repository

import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tables.Category.CATEGORY
import rankifyHub.tables.Item.ITEM

@Repository
class CategoryRepository(private val dsl: DSLContext) : CategoryRepository {

  override fun findAll(): List<Category> {
    val records =
      dsl
        .select()
        .from(CATEGORY)
        .leftJoin(ITEM)
        .on(CATEGORY.ID.eq(ITEM.CATEGORY_ID))
        .fetchGroups(CATEGORY.ID)

    return records.map { (_, groupRecords) ->
      val categoryRecord = groupRecords.first().into(CATEGORY)
      val itemRecords =
        groupRecords.mapNotNull { rec -> rec.into(ITEM).takeIf { it[ITEM.ID] != null } }

      Category.reconstruct(
        id = categoryRecord[CATEGORY.ID]!!,
        name = categoryRecord[CATEGORY.NAME]!!,
        description = categoryRecord[CATEGORY.DESCRIPTION],
        imagePath = categoryRecord[CATEGORY.IMAGE],
        items =
          itemRecords.map { itemRec ->
            Item.reconstruct(
              id = itemRec[ITEM.ID]!!,
              name = itemRec[ITEM.NAME]!!,
              imagePath = itemRec[ITEM.IMAGE],
              description = itemRec[ITEM.DESCRIPTION]
            )
          }
      )
    }
  }

  override fun findById(id: UUID): Optional<Category> {
    val categoryRecord =
      dsl.selectFrom(CATEGORY).where(CATEGORY.ID.eq(id)).fetchOne() ?: return Optional.empty()

    val itemRecords = dsl.selectFrom(ITEM).where(ITEM.CATEGORY_ID.eq(id)).fetch()

    val category =
      Category.reconstruct(
        id = categoryRecord[CATEGORY.ID]!!,
        name = categoryRecord[CATEGORY.NAME]!!,
        description = categoryRecord[CATEGORY.DESCRIPTION],
        imagePath = categoryRecord[CATEGORY.IMAGE],
        items =
          itemRecords.map { itemRec ->
            Item.reconstruct(
              id = itemRec[ITEM.ID]!!,
              name = itemRec[ITEM.NAME]!!,
              imagePath = itemRec[ITEM.IMAGE],
              description = itemRec[ITEM.DESCRIPTION]
            )
          }
      )

    return Optional.of(category)
  }

  override fun save(category: Category): Category {
    // 1) CategoryをUpsertする
    dsl
      .insertInto(CATEGORY)
      .set(CATEGORY.ID, category.id)
      .set(CATEGORY.NAME, category.name)
      .set(CATEGORY.DESCRIPTION, category.description)
      .set(CATEGORY.IMAGE, category.imagePath)
      .onDuplicateKeyUpdate()
      .set(CATEGORY.NAME, category.name)
      .set(CATEGORY.DESCRIPTION, category.description)
      .set(CATEGORY.IMAGE, category.imagePath)
      .execute()

    // 2) Itemを削除・追加
    dsl.deleteFrom(ITEM).where(ITEM.CATEGORY_ID.eq(category.id)).execute()

    category.items.forEach { item ->
      dsl
        .insertInto(ITEM)
        .set(ITEM.ID, item.id)
        .set(ITEM.NAME, item.name)
        .set(ITEM.IMAGE, item.imagePath)
        .set(ITEM.DESCRIPTION, item.description)
        .set(ITEM.CATEGORY_ID, category.id) // ここで関連付けを設定
        .execute()
    }

    return category
  }

  override fun deleteById(id: UUID) {
    // Itemの削除
    dsl.deleteFrom(ITEM).where(ITEM.CATEGORY_ID.eq(id)).execute()

    // Categoryの削除
    dsl.deleteFrom(CATEGORY).where(CATEGORY.ID.eq(id)).execute()
  }
}
