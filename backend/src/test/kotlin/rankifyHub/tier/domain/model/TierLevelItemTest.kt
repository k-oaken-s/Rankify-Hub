package rankifyHub.tier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.OrderIndex

class TierLevelItemTest :
  StringSpec({
    "順序を更新できることを確認" {
      val tierLevelId = UUID.randomUUID()
      val tierId = UUID.randomUUID()
      val itemId = UUID.randomUUID()
      val initialOrderIndex = OrderIndex(1)
      val createdAt = Instant.now()
      val updatedAt = Instant.now()

      val item =
        TierLevelItem(
          id = UUID.randomUUID(),
          tierLevelId = tierLevelId,
          tierId = tierId,
          itemId = itemId,
          orderIndex = initialOrderIndex,
          createdAt = createdAt,
          updatedAt = updatedAt
        )

      item.updateOrder(OrderIndex(2))

      item.orderIndex.value shouldBe 2
    }
  })
