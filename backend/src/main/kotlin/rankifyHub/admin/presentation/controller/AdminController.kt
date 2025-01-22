package rankifyHub.admin.presentation.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rankifyHub.admin.application.AdminAuthenticationUseCase

/** RESTコントローラー: 管理者認証を処理します。 */
@RestController
@RequestMapping("/admin")
class AdminController(private val adminAuthenticationUseCase: AdminAuthenticationUseCase) {
  data class LoginRequest(val username: String, val password: String)

  data class LoginResponse(val token: String)

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

  @GetMapping("/protected")
  fun getProtectedResource(): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(mapOf("message" to "This is a protected resource"))
  }
}
