package rankifyHub.tier.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.repository.TierRepository

class GetTierUseCaseTest :
  DescribeSpec({
    lateinit var tierRepository: TierRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var getTierUseCase: GetTierUseCase

    beforeTest {
      tierRepository = mockk()
      categoryRepository = mockk()
      getTierUseCase = GetTierUseCase(tierRepository, categoryRepository)
    }

    describe("getTierById") {
      val tierId = UUID.randomUUID()
      val categoryId = UUID.randomUUID()

      context("when both tier and category exist") {
        it("should return TierWithCategory") {
          val tier = mockk<Tier>()
          val category = mockk<Category>()
          val expectedTierWithCategory = TierWithCategory(tier, category)

          every { tier.categoryId } returns categoryId
          every { tierRepository.findById(tierId) } returns tier
          every { categoryRepository.findById(categoryId) } returns category

          val result = getTierUseCase.getTierById(tierId)

          result shouldBe expectedTierWithCategory

          verify(exactly = 1) {
            tierRepository.findById(tierId)
            categoryRepository.findById(categoryId)
          }
        }
      }

      context("when tier does not exist") {
        it("should throw NoSuchElementException") {
          every { tierRepository.findById(tierId) } returns null

          shouldThrow<NoSuchElementException> { getTierUseCase.getTierById(tierId) }
            .message shouldBe "Tier not found"

          verify(exactly = 1) { tierRepository.findById(tierId) }
          verify(exactly = 0) { categoryRepository.findById(any()) }
        }
      }

      context("when tier exists but category does not exist") {
        it("should throw NoSuchElementException") {
          val tier = mockk<Tier>()

          every { tier.categoryId } returns categoryId
          every { tierRepository.findById(tierId) } returns tier
          every { categoryRepository.findById(categoryId) } returns null

          shouldThrow<NoSuchElementException> { getTierUseCase.getTierById(tierId) }
            .message shouldBe "Category not found"

          verify(exactly = 1) {
            tierRepository.findById(tierId)
            categoryRepository.findById(categoryId)
          }
        }
      }
    }
  })
