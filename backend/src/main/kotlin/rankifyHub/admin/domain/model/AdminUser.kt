package rankifyHub.admin.domain.model

import java.util.*
import org.springframework.security.crypto.password.PasswordEncoder

/** 管理者ユーザーを表すドメインオブジェクト */
class AdminUser
private constructor(val id: UUID, val username: String, private val hashedPassword: String) {

  /** ハッシュ化されたパスワードを取得 */
  fun getHashedPassword(): String = hashedPassword

  companion object {
    /** 管理者を新規作成 */
    fun create(username: String, rawPassword: String, passwordEncoder: PasswordEncoder): AdminUser {
      return AdminUser(
        id = UUID.randomUUID(),
        username = username,
        hashedPassword = passwordEncoder.encode(rawPassword)
      )
    }

    /** 管理者を再作成 */
    fun reconstruct(id: UUID, username: String, hashedPassword: String): AdminUser {
      return AdminUser(id, username, hashedPassword)
    }
  }

  /**
   * パスワードを検証
   *
   * @return 検証結果
   */
  fun verifyPassword(rawPassword: String, passwordEncoder: PasswordEncoder): Boolean {
    return passwordEncoder.matches(rawPassword, hashedPassword)
  }
}
