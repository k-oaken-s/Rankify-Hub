package rankifyHub.admin.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import rankifyHub.admin.domain.model.AdminUser
import rankifyHub.admin.domain.repository.AdminUserRepository

@Component
class AdminUserInitializer(
  private val adminUserRepository: AdminUserRepository,
  private val passwordEncoder: PasswordEncoder,
  @Value("\${admin.initial.username:admin}") private val initialUsername: String,
  @Value("\${admin.initial.password:#{null}}") private val initialPassword: String?
) : CommandLineRunner {

  override fun run(vararg args: String) {
    if (!initialPassword.isNullOrBlank()) {
      val existingUser = adminUserRepository.findByUsername(initialUsername)
      if (existingUser == null) {
        val adminUser =
          AdminUser.create(
            username = initialUsername,
            rawPassword = initialPassword,
            passwordEncoder = passwordEncoder
          )
        adminUserRepository.save(adminUser)
        println("Initial admin user created: $initialUsername")
      }
    }
  }
}
