package rankifyHub.admin.domain.repository

import rankifyHub.admin.domain.model.AdminUser

/** 管理者ユーザーリポジトリインターフェース */
interface AdminUserRepository {

  /** ユーザー名で管理者を検索 */
  fun findByUsername(username: String): AdminUser?

  /** 管理者を保存 */
  fun save(adminUser: AdminUser): AdminUser
}
