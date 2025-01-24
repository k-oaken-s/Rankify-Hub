package rankifyHub.tier.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.*
import rankifyHub.tier.domain.vo.OrderIndex

class TierTest :
  StringSpec({
    "レベルを追加できることを確認" {
      val tier = mockk<Tier>(relaxed = true)
      val levels = mutableListOf<TierLevel>()
      every { tier.getLevels() } returns levels

      val level = mockk<TierLevel>(relaxed = true)
      every { tier.addLevel(level) } answers
        {
          levels.add(level)
          every { level.orderIndex = OrderIndex(levels.size) } just runs
        }

      tier.addLevel(level)

      tier.getLevels() shouldHaveSize 1
      tier.getLevels() shouldContain level
    }

    "レベルの順序を並べ替えられることを確認" {
      val levels = mutableListOf<TierLevel>()
      val tier = mockk<Tier>(relaxed = true)
      val level1 = mockk<TierLevel>(relaxed = true)
      val level2 = mockk<TierLevel>(relaxed = true)

      levels.addAll(listOf(level1, level2))
      every { tier.getLevels() } returns levels

      every { tier.removeLevel(level1) } answers
        {
          levels.remove(level1)
          levels.forEachIndexed { index, level ->
            every { level.orderIndex = OrderIndex(index + 1) } just Runs
            level.orderIndex = OrderIndex(index + 1)
          }
        }

      tier.removeLevel(level1)

      tier.getLevels() shouldHaveSize 1
      tier.getLevels() shouldContain level2
      verify { level2.orderIndex = OrderIndex(1) }
    }
  })
