package rankifyHub.userTier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.*
import rankifyHub.userTier.domain.vo.OrderIndex

class UserTierTest :
  StringSpec({
    "レベルを追加できることを確認" {
      val levels = mutableListOf<UserTierLevel>()
      val userTier = mockk<UserTier>(relaxed = true)
      val level = mockk<UserTierLevel>(relaxed = true)

      every { userTier.getLevels() } returns levels
      every { userTier.addLevel(level) } answers
        {
          levels.add(level)
          every { level.userTier = userTier } just Runs
          every { level.orderIndex = OrderIndex(levels.size) } just Runs
          level.userTier = userTier
          level.orderIndex = OrderIndex(levels.size)
        }

      userTier.addLevel(level)

      userTier.getLevels() shouldHaveSize 1
      userTier.getLevels() shouldContain level
      verify { level.userTier = userTier }
      verify { level.orderIndex = OrderIndex(1) }
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
