package rankifyHub.admin.domain.model

import java.util.*
import org.springframework.security.crypto.password.PasswordEncoder

class AdminUser
private constructor(val id: UUID, val username: String, private val hashedPassword: String) {
  fun getHashedPassword(): String = hashedPassword

  companion object {
    fun create(username: String, rawPassword: String, passwordEncoder: PasswordEncoder): AdminUser {
      return AdminUser(
        id = UUID.randomUUID(),
        username = username,
        hashedPassword = passwordEncoder.encode(rawPassword)
      )
    }

    fun reconstruct(id: UUID, username: String, hashedPassword: String): AdminUser {
      return AdminUser(id, username, hashedPassword)
    }
  }

  fun verifyPassword(rawPassword: String, passwordEncoder: PasswordEncoder): Boolean {
    return passwordEncoder.matches(rawPassword, hashedPassword)
  }
}
