package rankifyHub.tier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.*
import rankifyHub.tier.domain.vo.OrderIndex

class TierLevelTest :
  StringSpec({
    "アイテムを追加できることを確認" {
      val tierLevel =
        TierLevel(
          id = UUID.randomUUID(),
          tierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item =
        TierLevelItem(
          id = UUID.randomUUID(),
          tierLevelId = tierLevel.id,
          tierId = tierLevel.tierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      tierLevel.addItem(item)

      tierLevel.items shouldHaveSize 1
      tierLevel.items shouldContain item
      item.orderIndex.value shouldBe 1
      item.tierLevelId shouldBe tierLevel.id
      item.tierId shouldBe tierLevel.tierId
    }

    "アイテムを削除できることを確認" {
      val tierLevel =
        TierLevel(
          id = UUID.randomUUID(),
          tierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item =
        TierLevelItem(
          id = UUID.randomUUID(),
          tierLevelId = tierLevel.id,
          tierId = tierLevel.tierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      tierLevel.addItem(item)
      tierLevel.removeItem(item)

      tierLevel.items shouldHaveSize 0
    }

    "アイテムの順序を並べ替えられることを確認" {
      val tierLevel =
        TierLevel(
          id = UUID.randomUUID(),
          tierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item1 =
        TierLevelItem(
          id = UUID.randomUUID(),
          tierLevelId = tierLevel.id,
          tierId = tierLevel.tierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )
      val item2 =
        TierLevelItem(
          id = UUID.randomUUID(),
          tierLevelId = tierLevel.id,
          tierId = tierLevel.tierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(2),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      tierLevel.addItem(item1)
      tierLevel.addItem(item2)

      tierLevel.items[0].orderIndex.value shouldBe 1
      tierLevel.items[1].orderIndex.value shouldBe 2

      tierLevel.removeItem(item1)

      tierLevel.items[0].orderIndex.value shouldBe 1
    }
  })
