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
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.repository.UserTierRepository

class GetTierUseCaseTest :
  DescribeSpec({
    lateinit var userTierRepository: UserTierRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var getTierUseCase: GetTierUseCase

    beforeTest {
      userTierRepository = mockk()
      categoryRepository = mockk()
      getTierUseCase = GetTierUseCase(userTierRepository, categoryRepository)
    }

    describe("getUserTierById") {
      val userTierId = UUID.randomUUID()
      val categoryId = UUID.randomUUID()

      context("when both userTier and category exist") {
        it("should return TierWithCategory") {
          val userTier = mockk<UserTier>()
          val category = mockk<Category>()
          val expectedTierWithCategory = TierWithCategory(userTier, category)

          every { userTier.categoryId } returns categoryId
          every { userTierRepository.findById(userTierId) } returns userTier
          every { categoryRepository.findById(categoryId) } returns category

          val result = getTierUseCase.getUserTierById(userTierId)

          result shouldBe expectedTierWithCategory

          verify(exactly = 1) {
            userTierRepository.findById(userTierId)
            categoryRepository.findById(categoryId)
          }
        }
      }

      context("when userTier does not exist") {
        it("should throw NoSuchElementException") {
          every { userTierRepository.findById(userTierId) } returns null

          shouldThrow<NoSuchElementException> { getTierUseCase.getUserTierById(userTierId) }
            .message shouldBe "UserTier not found"

          verify(exactly = 1) { userTierRepository.findById(userTierId) }
          verify(exactly = 0) { categoryRepository.findById(any()) }
        }
      }

      context("when userTier exists but category does not exist") {
        it("should throw NoSuchElementException") {
          val userTier = mockk<UserTier>()

          every { userTier.categoryId } returns categoryId
          every { userTierRepository.findById(userTierId) } returns userTier
          every { categoryRepository.findById(categoryId) } returns null

          shouldThrow<NoSuchElementException> { getTierUseCase.getUserTierById(userTierId) }
            .message shouldBe "Category not found"

          verify(exactly = 1) {
            userTierRepository.findById(userTierId)
            categoryRepository.findById(categoryId)
          }
        }
      }
    }
  })
