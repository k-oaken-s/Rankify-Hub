package rankifyHub.admin.application

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import rankifyHub.admin.domain.repository.AdminUserRepository
import rankifyHub.common.security.JwtUtil

@Service
class AdminAuthenticationUseCase(
  private val adminUserRepository: AdminUserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val jwtUtil: JwtUtil
) {
  fun authenticate(username: String, password: String): String? {
    val user = adminUserRepository.findByUsername(username) ?: return null
    return if (user.verifyPassword(password, passwordEncoder)) {
      jwtUtil.generateToken(user.username)
    } else {
      null
    }
  }

  fun validateToken(token: String): String? {
    return jwtUtil.validateTokenAndGetUsername(token)
  }
}
