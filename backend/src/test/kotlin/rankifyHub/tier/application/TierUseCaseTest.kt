package rankifyHub.tier.application

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.tier.domain.model.Tier
import rankifyHub.tier.domain.model.TierFactory
import rankifyHub.tier.domain.model.TierLevel
import rankifyHub.tier.domain.repository.TierRepository
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.TierName
import rankifyHub.tier.presentation.dto.CreateTierRequest
import rankifyHub.tier.presentation.dto.TierItemRequest
import rankifyHub.tier.presentation.dto.TierLevelRequest

class TierUseCaseTest :
  DescribeSpec({
    lateinit var tierRepository: TierRepository
    lateinit var tierFactory: TierFactory
    lateinit var fileStorageRepository: FileStorageRepository
    lateinit var tierUseCase: TierUseCase

    val categoryId = UUID.randomUUID()
    val anonymousId = "test-anonymous-id"
    val tierName = "Test Tier"

    beforeTest {
      tierRepository = mockk()
      tierFactory = mockk()
      fileStorageRepository = mockk()
      tierUseCase = TierUseCase(tierRepository, tierFactory, fileStorageRepository)
    }

    describe("create") {
      it("should create a user tier with levels and items") {
        val request =
          CreateTierRequest(
            anonymousId = anonymousId,
            categoryId = categoryId.toString(),
            name = tierName,
            isPublic = true,
            levels =
              listOf(
                TierLevelRequest(
                  name = "S",
                  orderIndex = 0,
                  items =
                    listOf(TierItemRequest(itemId = UUID.randomUUID().toString(), orderIndex = 0))
                )
              )
          )

        val tierSlot = slot<Tier>()
        val expectedTier = mockk<Tier>()

        every {
          tierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = TierName(request.name),
            isPublic = request.isPublic,
            any<List<TierLevel>>()
          )
        } returns expectedTier

        every { tierRepository.save(capture(tierSlot)) } returns expectedTier

        val result = tierUseCase.create(request)

        result shouldBe expectedTier

        verify(exactly = 1) {
          tierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = TierName(request.name),
            isPublic = request.isPublic,
            any<List<TierLevel>>()
          )
        }
        verify(exactly = 1) { tierRepository.save(expectedTier) }
      }

      it("should handle empty levels list") {
        val request =
          CreateTierRequest(
            anonymousId = anonymousId,
            categoryId = categoryId.toString(),
            name = tierName,
            isPublic = true,
            levels = emptyList()
          )

        val expectedTier = mockk<Tier>()

        every {
          tierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = TierName(request.name),
            isPublic = request.isPublic,
            levels = emptyList()
          )
        } returns expectedTier

        every { tierRepository.save(expectedTier) } returns expectedTier

        val result = tierUseCase.create(request)

        result shouldBe expectedTier

        verify(exactly = 1) {
          tierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = TierName(request.name),
            isPublic = request.isPublic,
            levels = emptyList()
          )
        }
      }

      context("when creating a private tier") {
        it("should create a private user tier") {
          val request =
            CreateTierRequest(
              anonymousId = anonymousId,
              categoryId = categoryId.toString(),
              name = tierName,
              isPublic = false,
              levels = emptyList()
            )

          val expectedTier = mockk<Tier>()

          every {
            tierFactory.create(
              anonymousId = AnonymousId(request.anonymousId),
              categoryId = UUID.fromString(request.categoryId),
              name = TierName(request.name),
              isPublic = false,
              levels = emptyList()
            )
          } returns expectedTier

          every { tierRepository.save(expectedTier) } returns expectedTier

          val result = tierUseCase.create(request)

          result shouldBe expectedTier
        }
      }
    }
  })
