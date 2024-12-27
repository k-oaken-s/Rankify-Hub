package rankifyHub.category.domain.repository

import rankifyHub.category.domain.model.Item

interface ItemRepository {
  fun findLatest(limit: Int): List<Item>

  fun findByCustomCondition(condition: String): List<Item>
}
