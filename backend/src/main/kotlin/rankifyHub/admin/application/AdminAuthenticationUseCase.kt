package rankifyHub.admin.application

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import rankifyHub.admin.domain.repository.AdminUserRepository
import rankifyHub.common.security.JwtUtil

/** 管理者認証を行うユースケース */
@Service
class AdminAuthenticationUseCase(
  private val adminUserRepository: AdminUserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val jwtUtil: JwtUtil
) {

  /**
   * ユーザー名とパスワードで認証し、JWTトークンを生成
   *
   * @return 認証成功時はJWTトークン、失敗時はnull
   */
  fun authenticate(username: String, password: String): String? {
    val user = adminUserRepository.findByUsername(username) ?: return null
    return if (user.verifyPassword(password, passwordEncoder)) {
      jwtUtil.generateToken(user.username)
    } else {
      null
    }
  }

  /**
   * JWTトークンを検証し、ユーザー名を取得
   *
   * @return トークンが有効な場合はユーザー名、無効な場合はnull
   */
  fun validateToken(token: String): String? {
    return jwtUtil.validateTokenAndGetUsername(token)
  }
}
