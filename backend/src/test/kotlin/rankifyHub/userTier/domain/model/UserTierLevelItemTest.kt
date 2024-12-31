package rankifyHub.userTier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.OrderIndex

class UserTierLevelItemTest :
  StringSpec({
    "順序を更新できることを確認" {
      val userTierLevelId = UUID.randomUUID()
      val userTierId = UUID.randomUUID()
      val itemId = UUID.randomUUID()
      val initialOrderIndex = OrderIndex(1)
      val createdAt = Instant.now()
      val updatedAt = Instant.now()

      val item =
        UserTierLevelItem(
          id = UUID.randomUUID(),
          userTierLevelId = userTierLevelId,
          userTierId = userTierId,
          itemId = itemId,
          orderIndex = initialOrderIndex,
          createdAt = createdAt,
          updatedAt = updatedAt
        )

      // 順序を変更
      item.updateOrder(OrderIndex(2))

      item.orderIndex.value shouldBe 2
    }
  })
