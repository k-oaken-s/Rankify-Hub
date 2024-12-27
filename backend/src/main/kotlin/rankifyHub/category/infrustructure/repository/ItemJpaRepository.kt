package rankifyHub.category.infrastructure.repository

import java.util.*
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.model.Item
import rankifyHub.category.domain.repository.ItemRepository
import rankifyHub.jooq.generated.tables.ItemTable

@Repository
class ItemJooqRepository(private val dsl: DSLContext) : ItemRepository {

  override fun findLatest(limit: Int): List<Item> {
    // 更新日時などを持っている前提で order by することを想定
    // ここでは単純にID降順という例
    val records =
      dsl.selectFrom(ItemTable.ITEM).orderBy(ItemTable.ITEM.ID.desc()).limit(limit).fetch()

    return records.map { record ->
      // categoryとの紐付けは必須なので仮のCategory (ただし本来はCategoryテーブルもJOINなどで取得)
      val dummyCategory =
        Category.reconstruct(
          id = UUID.randomUUID(),
          name = "dummy",
          description = null,
          imagePath = null,
          items = emptyList()
        )
      Item.reconstruct(
        id = UUID.fromString(record.id),
        name = record.name,
        imagePath = record.imagePath,
        description = record.description,
        category = dummyCategory
      )
    }
  }

  override fun findByCustomCondition(condition: String): List<Item> {
    // conditionをどう扱うかは実装次第。
    // 例: 名前LIKE検索する等。SQLインジェクションに注意！
    val records =
      dsl
        .selectFrom(ItemTable.ITEM)
        .where(ItemTable.ITEM.NAME.likeIgnoreCase("%$condition%"))
        .fetch()

    return records.map { record ->
      val dummyCategory =
        Category.reconstruct(
          id = UUID.randomUUID(),
          name = "dummy",
          description = null,
          imagePath = null,
          items = emptyList()
        )
      Item.reconstruct(
        id = UUID.fromString(record.id),
        name = record.name,
        imagePath = record.imagePath,
        description = record.description,
        category = dummyCategory
      )
    }
  }
}
