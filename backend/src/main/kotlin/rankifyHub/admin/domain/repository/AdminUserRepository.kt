package rankifyHub.admin.domain.repository

import rankifyHub.admin.domain.model.AdminUser

interface AdminUserRepository {
  fun findByUsername(username: String): AdminUser?

  fun save(adminUser: AdminUser): AdminUser
}
