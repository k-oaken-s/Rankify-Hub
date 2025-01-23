package rankifyHub.admin.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rankifyHub.admin.application.AdminAuthenticationUseCase

/** 管理者認証のREST APIエンドポイント */
@RestController
@RequestMapping("/admin")
class AdminController(private val adminAuthenticationUseCase: AdminAuthenticationUseCase) {

  /** ログインリクエスト */
  data class LoginRequest(val username: String, val password: String)

  /** ログインレスポンス */
  data class LoginResponse(val token: String)

  /**
   * 管理者ログイン
   *
   * @return 認証成功時はJWTトークン、失敗時は401エラー
   */
  @PostMapping("/login")
  fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
    val token =
      adminAuthenticationUseCase.authenticate(
        username = request.username,
        password = request.password
      )
        ?: return ResponseEntity.status(401).build()

    return ResponseEntity.ok(LoginResponse(token))
  }

  /**
   * 保護されたリソースへのアクセス確認用エンドポイント
   *
   * @return 認証成功メッセージ
   */
  @GetMapping("/protected")
  fun getProtectedResource(): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to "This is a protected resource"))
  }
}
