package rankifyHub.userTier.presentation.controller

import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.multipart.MultipartFile
import rankifyHub.userTier.application.CreateUserTierUseCase
import rankifyHub.userTier.application.GetLatestUserTiersUseCase
import rankifyHub.userTier.presentation.dto.CreateUserTierRequest
import rankifyHub.userTier.presentation.dto.UserTierDetailResponse
import rankifyHub.userTier.presentation.dto.UserTierResponse
import rankifyHub.userTier.presentation.presenter.UserTierPresenter

@RestController
@RequestMapping("/user-tiers")
class UserTierController(
  private val createUserTierUseCase: CreateUserTierUseCase,
  private val getLatestUserTiersUseCase: GetLatestUserTiersUseCase,
  private val presenter: UserTierPresenter
) {

  @PostMapping
  fun create(
    @RequestPart("request") request: CreateUserTierRequest,
    @RequestPart("image", required = false) imageFile: MultipartFile?
  ): ResponseEntity<UserTierDetailResponse> {
    // CreateUserTierUseCase にリクエストとファイルを渡す
    val userTier = createUserTierUseCase.create(request, imageFile)
    return ResponseEntity.ok(UserTierDetailResponse.fromEntity(userTier))
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
