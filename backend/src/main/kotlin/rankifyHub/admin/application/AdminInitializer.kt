package rankifyHub.admin.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import rankifyHub.admin.domain.model.AdminUser
import rankifyHub.admin.domain.repository.AdminUserRepository

/** 管理者の初期アカウントを作成する起動時初期化コンポーネント */
@Component
class AdminUserInitializer(
  private val adminUserRepository: AdminUserRepository,
  private val passwordEncoder: PasswordEncoder,
  @Value("\${admin.initial.username:admin}") private val initialUsername: String,
  @Value("\${admin.initial.password:#{null}}") private val initialPassword: String?
) : CommandLineRunner {

  /**
   * アプリケーション起動時に実行
   * - パスワードが設定されている場合のみ初期化を実行
   * - 既存のユーザーが存在しない場合のみ作成
   */
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
