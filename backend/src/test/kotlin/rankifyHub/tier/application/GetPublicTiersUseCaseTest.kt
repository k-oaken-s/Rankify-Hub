package rankifyHub.tier.application

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.*
import rankifyHub.category.domain.model.Category
import rankifyHub.category.domain.repository.CategoryRepository
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.repository.UserTierRepository

class GetPublicTiersUseCaseTest :
  DescribeSpec({
    lateinit var userTierRepository: UserTierRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var getPublicTiersUseCase: GetPublicTiersUseCase

    beforeTest {
      userTierRepository = mockk()
      categoryRepository = mockk()
      getPublicTiersUseCase = GetPublicTiersUseCase(userTierRepository, categoryRepository)
    }

    fun createMockUserTier(isPublic: Boolean = true): UserTier {
      return mockk<UserTier>().also {
        every { it.isPublic } returns isPublic
        every { it.categoryId } returns UUID.randomUUID()
      }
    }

    describe("getRecent") {
      it("should return only public tiers with their categories") {
        val publicTier1 = createMockUserTier(true)
        val publicTier2 = createMockUserTier(true)
        val privateTier = createMockUserTier(false)

        val category1 = mockk<Category>()
        val category2 = mockk<Category>()

        every { userTierRepository.findAllOrderByCreatedAtDesc() } returns
          listOf(publicTier1, privateTier, publicTier2)
        every { categoryRepository.findById(publicTier1.categoryId) } returns category1
        every { categoryRepository.findById(publicTier2.categoryId) } returns category2

        val result = getPublicTiersUseCase.getRecent()

        result shouldBe
          listOf(TierWithCategory(publicTier1, category1), TierWithCategory(publicTier2, category2))

        verify {
          userTierRepository.findAllOrderByCreatedAtDesc()
          categoryRepository.findById(publicTier1.categoryId)
          categoryRepository.findById(publicTier2.categoryId)
        }
      }

      it("should handle missing categories") {
        val publicTier = createMockUserTier(true)

        every { userTierRepository.findAllOrderByCreatedAtDesc() } returns listOf(publicTier)
        every { categoryRepository.findById(publicTier.categoryId) } returns null

        val result = getPublicTiersUseCase.getRecent()

        result shouldBe emptyList()
      }
    }

    describe("getRecentWithLimit") {
      it("should return limited number of public tiers") {
        val limit = 2
        val publicTier = createMockUserTier(true)
        val category = mockk<Category>()

        every { userTierRepository.findLatest(limit) } returns listOf(publicTier)
        every { categoryRepository.findById(publicTier.categoryId) } returns category

        val result = getPublicTiersUseCase.getRecentWithLimit(limit)

        result shouldBe listOf(TierWithCategory(publicTier, category))

        verify {
          userTierRepository.findLatest(limit)
          categoryRepository.findById(publicTier.categoryId)
        }
      }
    }

    describe("getCreatedAfter") {
      it("should return tiers created after specified timestamp") {
        val timestamp = Instant.now()
        val publicTier = createMockUserTier(true)
        val category = mockk<Category>()

        every { userTierRepository.findSince(timestamp) } returns listOf(publicTier)
        every { categoryRepository.findById(publicTier.categoryId) } returns category

        val result = getPublicTiersUseCase.getCreatedAfter(timestamp)

        result shouldBe listOf(TierWithCategory(publicTier, category))

        verify {
          userTierRepository.findSince(timestamp)
          categoryRepository.findById(publicTier.categoryId)
        }
      }

      it("should handle empty result") {
        val timestamp = Instant.now()

        every { userTierRepository.findSince(timestamp) } returns emptyList()

        val result = getPublicTiersUseCase.getCreatedAfter(timestamp)

        result shouldBe emptyList()

        verify { userTierRepository.findSince(timestamp) }

        verify(exactly = 0) { categoryRepository.findById(any()) }
      }
    }

    describe("common behavior for all methods") {
      it("should filter out private tiers") {
        val timestamp = Instant.now()
        val privateTier = createMockUserTier(false)

        every { userTierRepository.findSince(timestamp) } returns listOf(privateTier)

        val result = getPublicTiersUseCase.getCreatedAfter(timestamp)

        result shouldBe emptyList()

        verify(exactly = 0) { categoryRepository.findById(any()) }
      }

      it("should handle null categories") {
        val publicTier1 = createMockUserTier(true)
        val publicTier2 = createMockUserTier(true)
        val category = mockk<Category>()

        every { userTierRepository.findAllOrderByCreatedAtDesc() } returns
          listOf(publicTier1, publicTier2)
        every { categoryRepository.findById(publicTier1.categoryId) } returns category
        every { categoryRepository.findById(publicTier2.categoryId) } returns null

        val result = getPublicTiersUseCase.getRecent()

        result shouldBe listOf(TierWithCategory(publicTier1, category))
      }
    }
  })
