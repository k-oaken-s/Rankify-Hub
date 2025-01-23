package rankifyHub.category.infrastructure.repository

import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tables.Category.CATEGORY
import rankifyHub.tables.Item.ITEM

/** カテゴリ集約のJOOQによる永続化実装。 カテゴリとそれに属するアイテムの整合性を保証する。 */
@Repository
class CategoryRepository(private val dsl: DSLContext) : CategoryRepository {

  /** 全カテゴリをアイテムと共に取得する。 */
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

  /** 指定IDのカテゴリをアイテムと共に取得する。 カテゴリが存在しない場合はnullを返す。 */
  override fun findById(id: UUID): Category? {
    val categoryRecord =
      dsl.selectFrom(CATEGORY).where(CATEGORY.ID.eq(id)).fetchOne() ?: return null

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

    return category
  }

  /**
   * カテゴリを保存する。
   *
   * @return 保存されたカテゴリ（入力と同一インスタンス）
   */
  override fun save(category: Category): Category {
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

    dsl.deleteFrom(ITEM).where(ITEM.CATEGORY_ID.eq(category.id)).execute()

    category.items.forEach { item ->
      dsl
        .insertInto(ITEM)
        .set(ITEM.ID, item.id)
        .set(ITEM.NAME, item.name)
        .set(ITEM.IMAGE, item.imagePath)
        .set(ITEM.DESCRIPTION, item.description)
        .set(ITEM.CATEGORY_ID, category.id)
        .execute()
    }

    return category
  }

  /** カテゴリとそれに属する全アイテムを削除する。 */
  override fun deleteById(id: UUID) {
    dsl.deleteFrom(ITEM).where(ITEM.CATEGORY_ID.eq(id)).execute()

    dsl.deleteFrom(CATEGORY).where(CATEGORY.ID.eq(id)).execute()
  }
}
