package rankifyHub.tier.presentation.controller

import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import rankifyHub.tier.application.GetPublicTiersUseCase
import rankifyHub.tier.application.GetTierUseCase
import rankifyHub.tier.application.TierUseCase
import rankifyHub.tier.presentation.dto.CreateTierRequest
import rankifyHub.tier.presentation.dto.TierDetailResponse
import rankifyHub.tier.presentation.dto.TierResponse
import rankifyHub.tier.presentation.presenter.TierPresenter

/** ティア関連のREST APIエンドポイントを提供するコントローラ */
@RestController
@RequestMapping("/tiers")
class TierController(
  private val tierUseCase: TierUseCase,
  private val getTierUseCase: GetTierUseCase,
  private val getPublicTiersUseCase: GetPublicTiersUseCase,
  private val presenter: TierPresenter
) {

  /** Tierを新規作成 */
  @PostMapping
  fun create(@RequestBody request: CreateTierRequest): ResponseEntity<String?> {
    val userTier = tierUseCase.create(request)
    return ResponseEntity.ok(userTier.id.toString())
  }

  /** 指定IDのTierを取得 */
  @GetMapping("/{tierId}")
  fun getUserTierById(@PathVariable tierId: UUID): ResponseEntity<TierDetailResponse> {
    val userTierWithCategory = getTierUseCase.getUserTierById(tierId)
    return ResponseEntity.ok(
      TierDetailResponse.fromEntity(userTierWithCategory.userTier, userTierWithCategory.category)
    )
  }

  /** 公開Tierの一覧を取得 */
  @GetMapping
  fun getPublicUserTiers(): List<TierResponse> {
    val userTiersWithCategory = getPublicTiersUseCase.getRecent()
    return userTiersWithCategory.map { presenter.toResponse(it) }
  }

  /** 最新の公開Tierを取得 */
  @GetMapping("/latest")
  fun getLatestUserTiers(@RequestParam limit: Int): List<TierResponse> {
    val userTiersWithCategory = getPublicTiersUseCase.getRecentWithLimit(limit)
    return userTiersWithCategory.map { presenter.toResponse(it) }
  }

  /**
   * 指定時刻以降に作成された公開Tierを取得（ロングポーリング）
   * - 30秒のタイムアウト
   * - 新規Tierが作成されるまで1秒間隔でポーリング
   * - タイムアウト時は空配列を返却
   */
  @GetMapping("/since")
  fun getUserTiersSince(@RequestParam since: Long): DeferredResult<List<TierResponse>> {
    val deferredResult = DeferredResult<List<TierResponse>>(30000L)
    val executor = Executors.newSingleThreadExecutor()

    CompletableFuture.runAsync(
        {
          val timestamp = Instant.ofEpochMilli(since)
          while (!deferredResult.isSetOrExpired) {
            val newUserTiers = getPublicTiersUseCase.getCreatedAfter(timestamp)
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
