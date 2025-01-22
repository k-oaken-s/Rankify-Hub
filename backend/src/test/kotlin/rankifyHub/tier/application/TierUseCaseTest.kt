package rankifyHub.tier.application

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.*
import rankifyHub.shared.domain.repository.FileStorageRepository
import rankifyHub.tier.domain.model.UserTier
import rankifyHub.tier.domain.model.UserTierFactory
import rankifyHub.tier.domain.model.UserTierLevel
import rankifyHub.tier.domain.repository.UserTierRepository
import rankifyHub.tier.domain.vo.AnonymousId
import rankifyHub.tier.domain.vo.UserTierName
import rankifyHub.tier.presentation.dto.CreateTierRequest
import rankifyHub.tier.presentation.dto.TierItemRequest
import rankifyHub.tier.presentation.dto.TierLevelRequest

class TierUseCaseTest :
  DescribeSpec({
    lateinit var userTierRepository: UserTierRepository
    lateinit var userTierFactory: UserTierFactory
    lateinit var fileStorageRepository: FileStorageRepository
    lateinit var tierUseCase: TierUseCase

    val categoryId = UUID.randomUUID()
    val anonymousId = "test-anonymous-id"
    val tierName = "Test Tier"

    beforeTest {
      userTierRepository = mockk()
      userTierFactory = mockk()
      fileStorageRepository = mockk()
      tierUseCase = TierUseCase(userTierRepository, userTierFactory, fileStorageRepository)
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

        val userTierSlot = slot<UserTier>()
        val expectedUserTier = mockk<UserTier>()

        every {
          userTierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = UserTierName(request.name),
            isPublic = request.isPublic,
            any<List<UserTierLevel>>()
          )
        } returns expectedUserTier

        every { userTierRepository.save(capture(userTierSlot)) } returns expectedUserTier

        val result = tierUseCase.create(request)

        result shouldBe expectedUserTier

        verify(exactly = 1) {
          userTierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = UserTierName(request.name),
            isPublic = request.isPublic,
            any<List<UserTierLevel>>()
          )
        }
        verify(exactly = 1) { userTierRepository.save(expectedUserTier) }
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

        val expectedUserTier = mockk<UserTier>()

        every {
          userTierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = UserTierName(request.name),
            isPublic = request.isPublic,
            levels = emptyList()
          )
        } returns expectedUserTier

        every { userTierRepository.save(expectedUserTier) } returns expectedUserTier

        val result = tierUseCase.create(request)

        result shouldBe expectedUserTier

        verify(exactly = 1) {
          userTierFactory.create(
            anonymousId = AnonymousId(request.anonymousId),
            categoryId = UUID.fromString(request.categoryId),
            name = UserTierName(request.name),
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

          val expectedUserTier = mockk<UserTier>()

          every {
            userTierFactory.create(
              anonymousId = AnonymousId(request.anonymousId),
              categoryId = UUID.fromString(request.categoryId),
              name = UserTierName(request.name),
              isPublic = false,
              levels = emptyList()
            )
          } returns expectedUserTier

          every { userTierRepository.save(expectedUserTier) } returns expectedUserTier

          val result = tierUseCase.create(request)

          result shouldBe expectedUserTier
        }
      }
    }
  })
