package rankifyHub.category.infrastructure.repository

import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.jooq.generated.tables.CategoryTable
import rankifyHub.jooq.generated.tables.ItemTable

@Repository
class CategoryJooqRepository(private val dsl: DSLContext) : CategoryRepository {

  override fun findAll(): List<Category> {
    // CategoryテーブルをJOINして、Itemもまとめて取得する例
    val records =
      dsl
        .select()
        .from(CategoryTable.CATEGORY)
        .leftJoin(ItemTable.ITEM)
        .on(CategoryTable.CATEGORY.ID.eq(ItemTable.ITEM.CATEGORY_ID))
        .fetchGroups(CategoryTable.CATEGORY.ID) // Category単位でGroup化

    return records.map { (categoryId, groupRecords) ->
      val categoryRecord = groupRecords[0].into(CategoryTable.CATEGORY)
      // Item群を取得
      val itemRecords =
        groupRecords.mapNotNull { rec ->
          rec.into(ItemTable.ITEM)?.takeIf { it.id != null } // leftJoinでnullになる場合あり
        }

      val category =
        Category.reconstruct(
          id = UUID.fromString(categoryRecord.id),
          name = categoryRecord.name,
          description = categoryRecord.description,
          imagePath = categoryRecord.imagePath,
          // item の再構築
          items =
            itemRecords.map { itemRec ->
              // categoryがまだ未完全に作れていないので、とりあえず「仮のCategory」で紐付け
              // 後でCategory自体が完成したら差し替えてもOK
              val dummyCategory =
                Category.reconstruct(
                  id = UUID.fromString(categoryRecord.id),
                  name = categoryRecord.name,
                  description = categoryRecord.description,
                  imagePath = categoryRecord.imagePath,
                  items = emptyList()
                )
              Item.reconstruct(
                id = UUID.fromString(itemRec.id),
                name = itemRec.name,
                imagePath = itemRec.imagePath,
                description = itemRec.description,
                category = dummyCategory
              )
            }
        )
      category
    }
  }

  override fun findById(id: UUID): Optional<Category> {
    // Category単体の取得
    val categoryRecord =
      dsl
        .selectFrom(CategoryTable.CATEGORY)
        .where(CategoryTable.CATEGORY.ID.eq(id.toString()))
        .fetchOne()
        ?: return Optional.empty()

    // Itemsも取得
    val itemRecords =
      dsl.selectFrom(ItemTable.ITEM).where(ItemTable.ITEM.CATEGORY_ID.eq(id.toString())).fetch()

    val category =
      Category.reconstruct(
        id = UUID.fromString(categoryRecord.id),
        name = categoryRecord.name,
        description = categoryRecord.description,
        imagePath = categoryRecord.imagePath,
        items =
          itemRecords.map { itemRec ->
            // Categoryは後で差し替えられるよう dummy でOK
            val dummyCategory =
              Category.reconstruct(
                id = UUID.fromString(categoryRecord.id),
                name = categoryRecord.name,
                description = categoryRecord.description,
                imagePath = categoryRecord.imagePath,
                items = emptyList()
              )
            Item.reconstruct(
              id = UUID.fromString(itemRec.id),
              name = itemRec.name,
              imagePath = itemRec.imagePath,
              description = itemRec.description,
              category = dummyCategory
            )
          }
      )
    return Optional.of(category)
  }

  override fun save(category: Category): Category {
    // 1) CategoryをUpsertする
    dsl
      .insertInto(CategoryTable.CATEGORY)
      .set(CategoryTable.CATEGORY.ID, category.id.toString())
      .set(CategoryTable.CATEGORY.NAME, category.name)
      .set(CategoryTable.CATEGORY.DESCRIPTION, category.description)
      .set(CategoryTable.CATEGORY.IMAGE_PATH, category.imagePath)
      .onDuplicateKeyUpdate()
      .set(CategoryTable.CATEGORY.NAME, category.name)
      .set(CategoryTable.CATEGORY.DESCRIPTION, category.description)
      .set(CategoryTable.CATEGORY.IMAGE_PATH, category.imagePath)
      .execute()

    // 2) Itemを削除・追加 (シンプルに一度全削除して再Insertするなど)
    dsl
      .deleteFrom(ItemTable.ITEM)
      .where(ItemTable.ITEM.CATEGORY_ID.eq(category.id.toString()))
      .execute()

    category.items.forEach { item ->
      dsl
        .insertInto(ItemTable.ITEM)
        .set(ItemTable.ITEM.ID, item.id.toString())
        .set(ItemTable.ITEM.NAME, item.name)
        .set(ItemTable.ITEM.IMAGE_PATH, item.imagePath)
        .set(ItemTable.ITEM.DESCRIPTION, item.description)
        .set(ItemTable.ITEM.CATEGORY_ID, category.id.toString())
        .execute()
    }
    return category
  }

  override fun deleteById(id: UUID) {
    // Itemの削除
    dsl.deleteFrom(ItemTable.ITEM).where(ItemTable.ITEM.CATEGORY_ID.eq(id.toString())).execute()

    // Categoryの削除
    dsl
      .deleteFrom(CategoryTable.CATEGORY)
      .where(CategoryTable.CATEGORY.ID.eq(id.toString()))
      .execute()
  }
}
