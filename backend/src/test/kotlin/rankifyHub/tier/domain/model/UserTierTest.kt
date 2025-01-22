package rankifyHub.tier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.*
import rankifyHub.tier.domain.vo.OrderIndex

class UserTierTest :
  StringSpec({
    "レベルを追加できることを確認" {
      val userTier = mockk<UserTier>(relaxed = true)
      val levels = mutableListOf<UserTierLevel>()
      every { userTier.getLevels() } returns levels

      val level = mockk<UserTierLevel>(relaxed = true)
      every { userTier.addLevel(level) } answers
        {
          levels.add(level)
          every { level.orderIndex = OrderIndex(levels.size) } just runs
        }

      userTier.addLevel(level)

      userTier.getLevels() shouldHaveSize 1
      userTier.getLevels() shouldContain level
    }

    "レベルの順序を並べ替えられることを確認" {
      val levels = mutableListOf<UserTierLevel>()
      val userTier = mockk<UserTier>(relaxed = true)
      val level1 = mockk<UserTierLevel>(relaxed = true)
      val level2 = mockk<UserTierLevel>(relaxed = true)

      levels.addAll(listOf(level1, level2))
      every { userTier.getLevels() } returns levels

      every { userTier.removeLevel(level1) } answers
        {
          levels.remove(level1)
          levels.forEachIndexed { index, level ->
            every { level.orderIndex = OrderIndex(index + 1) } just Runs
            level.orderIndex = OrderIndex(index + 1)
          }
        }

      userTier.removeLevel(level1)

      userTier.getLevels() shouldHaveSize 1
      userTier.getLevels() shouldContain level2
      verify { level2.orderIndex = OrderIndex(1) }
    }
  })
