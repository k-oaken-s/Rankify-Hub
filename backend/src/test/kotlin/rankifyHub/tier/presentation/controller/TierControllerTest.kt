package rankifyHub.tier.presentation.controller

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.*
import org.springframework.http.HttpStatus
import rankifyHub.category.domain.model.Category
import rankifyHub.tier.application.GetPublicTiersUseCase
import rankifyHub.tier.application.GetTierUseCase
import rankifyHub.tier.application.TierUseCase
import rankifyHub.tier.application.dto.TierWithCategory
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.vo.AccessUrl
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.TierName
import rankifyHub.tier.presentation.dto.CreateTierRequest
import rankifyHub.tier.presentation.dto.TierDetailResponse
import rankifyHub.tier.presentation.dto.TierResponse
import rankifyHub.tier.presentation.presenter.TierPresenter

class TierControllerTest :
  DescribeSpec({
    lateinit var tierUseCase: TierUseCase
    lateinit var getTierUseCase: GetTierUseCase
    lateinit var getPublicTiersUseCase: GetPublicTiersUseCase
    lateinit var presenter: TierPresenter
    lateinit var controller: TierController

    beforeTest {
      tierUseCase = mockk()
      getTierUseCase = mockk()
      getPublicTiersUseCase = mockk()
      presenter = mockk()
      controller = TierController(tierUseCase, getTierUseCase, getPublicTiersUseCase, presenter)
    }

    describe("create") {
      it("should create a new tier and return its ID") {
        val request = mockk<CreateTierRequest>()
        val tier = mockk<Tier>()
        val tierId = UUID.randomUUID()

        every { tierUseCase.create(request) } returns tier
        every { tier.id } returns tierId

        val response = controller.create(request)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe tierId.toString()

        verify { tierUseCase.create(request) }
      }
    }

    describe("getTierById") {
      it("should return tier detail response") {
        val tierId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val anonymousId = AnonymousId("test-anonymous-id")
        val tierName = TierName("Test Tier")
        val accessUrl = AccessUrl("test-url")

        val tier =
          mockk<Tier> {
            every { id } returns tierId
            every { this@mockk.categoryId } returns categoryId
            every { this@mockk.anonymousId.value } returns anonymousId.value
            every { this@mockk.name } returns tierName
            every { this@mockk.isPublic } returns true
            every { this@mockk.accessUrl.value } returns accessUrl.value
            every { createdAt } returns Instant.now()
            every { updatedAt } returns Instant.now()
            every { getLevels() } returns listOf()
          }

        val category =
          mockk<Category> {
            every { id } returns categoryId
            every { name } returns "Test Category"
            every { description } returns "Test Description"
            every { imagePath } returns null
            every { items } returns emptyList()
          }

        val tierWithCategory = TierWithCategory(tier, category)
        val expectedResponse =
          TierDetailResponse(
            id = tierId,
            anonymousId = anonymousId.value,
            categoryId = categoryId.toString(),
            categoryName = category.name,
            categoryImageUrl = category.imagePath,
            name = tierName.value,
            isPublic = true,
            accessUrl = accessUrl.value,
            levels = emptyList()
          )

        every { getTierUseCase.getTierById(tierId) } returns tierWithCategory

        val response = controller.getTierById(tierId)

        response.statusCode shouldBe HttpStatus.OK
        response.body shouldBe expectedResponse

        verify { getTierUseCase.getTierById(tierId) }
      }
    }

    describe("getPublicTiers") {
      it("should return list of public tiers") {
        val tierWithCategory = mockk<TierWithCategory>()
        val tierResponse = mockk<TierResponse>()

        every { getPublicTiersUseCase.getRecent() } returns listOf(tierWithCategory)
        every { presenter.toResponse(tierWithCategory) } returns tierResponse

        val result = controller.getPublicTiers()

        result shouldBe listOf(tierResponse)

        verify {
          getPublicTiersUseCase.getRecent()
          presenter.toResponse(tierWithCategory)
        }
      }
    }

    describe("getLatestTiers") {
      it("should return limited list of latest tiers") {
        val limit = 5
        val tierWithCategory = mockk<TierWithCategory>()
        val tierResponse = mockk<TierResponse>()

        every { getPublicTiersUseCase.getRecentWithLimit(limit) } returns listOf(tierWithCategory)
        every { presenter.toResponse(tierWithCategory) } returns tierResponse

        val result = controller.getLatestTiers(limit)

        result shouldBe listOf(tierResponse)

        verify {
          getPublicTiersUseCase.getRecentWithLimit(limit)
          presenter.toResponse(tierWithCategory)
        }
      }
    }

    describe("getTiersSince") {
      it("should return deferred result with tiers") {
        val timestamp = 1234567890L
        val tierWithCategory = mockk<TierWithCategory>()
        val tierResponse = mockk<TierResponse>()

        every { getPublicTiersUseCase.getCreatedAfter(any()) } returns listOf(tierWithCategory)
        every { presenter.toResponse(tierWithCategory) } returns tierResponse

        val result = controller.getTiersSince(timestamp)
        result shouldBe listOf(tierResponse)

        verify {
          getPublicTiersUseCase.getCreatedAfter(Instant.ofEpochMilli(timestamp))
          presenter.toResponse(tierWithCategory)
        }
      }

      it("should handle empty result") {
        val timestamp = 1234567890L

        every { getPublicTiersUseCase.getCreatedAfter(any()) } returns emptyList()

        controller.getTiersSince(timestamp)

        verify { getPublicTiersUseCase.getCreatedAfter(Instant.ofEpochMilli(timestamp)) }
      }
    }
  })
