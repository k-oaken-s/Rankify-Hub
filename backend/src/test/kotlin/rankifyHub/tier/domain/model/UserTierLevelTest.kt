package rankifyHub.tier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import rankifyHub.tier.domain.vo.OrderIndex
import java.time.Instant
import java.util.*

class UserTierLevelTest :
  StringSpec({
    "アイテムを追加できることを確認" {
      val userTierLevel =
        UserTierLevel(
          id = UUID.randomUUID(),
          userTierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item =
        UserTierLevelItem(
          id = UUID.randomUUID(),
          userTierLevelId = userTierLevel.id,
          userTierId = userTierLevel.userTierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      userTierLevel.addItem(item)

      userTierLevel.items shouldHaveSize 1
      userTierLevel.items shouldContain item
      item.orderIndex.value shouldBe 1
      item.userTierLevelId shouldBe userTierLevel.id
      item.userTierId shouldBe userTierLevel.userTierId
    }

    "アイテムを削除できることを確認" {
      val userTierLevel =
        UserTierLevel(
          id = UUID.randomUUID(),
          userTierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item =
        UserTierLevelItem(
          id = UUID.randomUUID(),
          userTierLevelId = userTierLevel.id,
          userTierId = userTierLevel.userTierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      userTierLevel.addItem(item)
      userTierLevel.removeItem(item)

      userTierLevel.items shouldHaveSize 0
    }

    "アイテムの順序を並べ替えられることを確認" {
      val userTierLevel =
        UserTierLevel(
          id = UUID.randomUUID(),
          userTierId = UUID.randomUUID(),
          name = "Test Level",
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      val item1 =
        UserTierLevelItem(
          id = UUID.randomUUID(),
          userTierLevelId = userTierLevel.id,
          userTierId = userTierLevel.userTierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )
      val item2 =
        UserTierLevelItem(
          id = UUID.randomUUID(),
          userTierLevelId = userTierLevel.id,
          userTierId = userTierLevel.userTierId,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(2),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      userTierLevel.addItem(item1)
      userTierLevel.addItem(item2)

      userTierLevel.items[0].orderIndex.value shouldBe 1
      userTierLevel.items[1].orderIndex.value shouldBe 2

      userTierLevel.removeItem(item1)

      userTierLevel.items[0].orderIndex.value shouldBe 1
    }
  })
