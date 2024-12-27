package rankifyHub.userTier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import java.time.Instant
import java.util.*
import rankifyHub.userTier.domain.vo.AccessUrl
import rankifyHub.userTier.domain.vo.AnonymousId
import rankifyHub.userTier.domain.vo.OrderIndex
import rankifyHub.userTier.domain.vo.UserTierName

class UserTierLevelTest :
  StringSpec({
    "アイテムを追加できることを確認" {
      val userTierLevel =
        UserTierLevel(
          id = UUID.randomUUID(),
          name = "Test Level",
          userTier = mockk(relaxed = true),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now(),
          _items = mutableListOf()
        )
      val userTier =
        UserTier(
          anonymousId = AnonymousId("test"),
          categoryId = UUID.randomUUID(),
          name = UserTierName("Test UserTier"),
          isPublic = true,
          accessUrl = AccessUrl("test-url")
        )

      val item =
        UserTierLevelItem(
          userTierLevel = userTierLevel,
          userTier = userTier,
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )

      userTierLevel.addItem(item)

      userTierLevel.items shouldHaveSize 1
      userTierLevel.items shouldContain item
      item.orderIndex.value shouldBe 1
      item.userTierLevel shouldBe userTierLevel
    }

    "アイテムを削除できることを確認" {
      val userTierLevel =
        UserTierLevel(
          id = UUID.randomUUID(),
          name = "Test Level",
          userTier = mockk(relaxed = true),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now(),
          _items = mutableListOf()
        )
      val item =
        UserTierLevelItem(
          userTierLevel = userTierLevel,
          userTier = mockk(relaxed = true),
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
          name = "Test Level",
          userTier = mockk(relaxed = true),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now(),
          _items = mutableListOf()
        )

      val item1 =
        UserTierLevelItem(
          userTierLevel = userTierLevel,
          userTier = mockk(relaxed = true),
          itemId = UUID.randomUUID(),
          orderIndex = OrderIndex(1),
          createdAt = Instant.now(),
          updatedAt = Instant.now()
        )
      val item2 =
        UserTierLevelItem(
          userTierLevel = userTierLevel,
          userTier = mockk(relaxed = true),
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
