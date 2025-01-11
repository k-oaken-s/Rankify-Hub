package rankifyHub.userTier.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import rankifyHub.userTier.application.CreateUserTierUseCase
import rankifyHub.userTier.application.GetLatestUserTiersUseCase
import rankifyHub.userTier.application.GetUserTierUseCase
import rankifyHub.userTier.presentation.dto.CreateUserTierRequest
import rankifyHub.userTier.presentation.dto.UserTierDetailResponse
import rankifyHub.userTier.presentation.dto.UserTierResponse
import rankifyHub.userTier.presentation.presenter.UserTierPresenter
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@RestController
@RequestMapping("/user-tiers")
class UserTierController(
  private val createUserTierUseCase: CreateUserTierUseCase,
  private val getUserTierUseCase: GetUserTierUseCase,
  private val getLatestUserTiersUseCase: GetLatestUserTiersUseCase,
  private val presenter: UserTierPresenter
) {

  @PostMapping
  fun create(@RequestBody request: CreateUserTierRequest): ResponseEntity<UserTierDetailResponse> {
    val userTier = createUserTierUseCase.create(request)
    return ResponseEntity.ok(UserTierDetailResponse.fromEntity(userTier))
  }

  @GetMapping("/{userTierId}")
  fun getUserTierById(@PathVariable userTierId: UUID): ResponseEntity<UserTierDetailResponse> {
    val userTier = getUserTierUseCase.getUserTierById(userTierId)
    val response = UserTierDetailResponse.fromEntity(userTier)
    return ResponseEntity.ok(response)
  }

  @GetMapping("/public")
  fun getPublicUserTiers(): List<UserTierResponse> {
    val userTiersWithCategory = getLatestUserTiersUseCase.getPublicUserTiers()
    return userTiersWithCategory.map { presenter.toResponse(it) }
  }

  @GetMapping("/latest")
  fun getLatestUserTiers(@RequestParam limit: Int): List<UserTierResponse> {
    val userTiersWithCategory = getLatestUserTiersUseCase.getLatestUserTiers(limit)
    return userTiersWithCategory.map { presenter.toResponse(it) }
  }

  @GetMapping("/since")
  fun getUserTiersSince(@RequestParam since: Long): DeferredResult<List<UserTierResponse>> {
    val deferredResult = DeferredResult<List<UserTierResponse>>(30000L)
    val executor = Executors.newSingleThreadExecutor()

    CompletableFuture.runAsync(
        {
          val timestamp = Instant.ofEpochMilli(since)
          while (!deferredResult.isSetOrExpired) {
            val newUserTiers = getLatestUserTiersUseCase.getUserTiersSince(timestamp)
            if (newUserTiers.isNotEmpty()) {
              val responses = newUserTiers.map { presenter.toResponse(it) }
              deferredResult.setResult(responses)
              break
            }
            Thread.sleep(1000)
          }
        },
        executor
      )
      .exceptionally { ex ->
        deferredResult.setErrorResult(ex)
        null
      }

    deferredResult.onTimeout { deferredResult.setResult(emptyList()) }

    return deferredResult
  }
}
