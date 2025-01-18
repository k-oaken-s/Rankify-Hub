package rankifyHub.admin.infrastructure.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.admin.domain.model.AdminUser
import rankifyHub.admin.domain.repository.AdminUserRepository
import rankifyHub.tables.AdminUsers.ADMIN_USERS

@Repository
class JooqAdminUserRepository(private val dsl: DSLContext) : AdminUserRepository {

  override fun findByUsername(username: String): AdminUser? {
    val record =
      dsl.selectFrom(ADMIN_USERS).where(ADMIN_USERS.USERNAME.eq(username)).fetchOne() ?: return null

    return AdminUser.reconstruct(
      id = record.get(ADMIN_USERS.ID),
      username = record.get(ADMIN_USERS.USERNAME),
      hashedPassword = record.get(ADMIN_USERS.HASHED_PASSWORD)
    )
  }

  override fun save(adminUser: AdminUser): AdminUser {
    dsl
      .insertInto(ADMIN_USERS)
      .set(ADMIN_USERS.ID, adminUser.id)
      .set(ADMIN_USERS.USERNAME, adminUser.username)
      .set(ADMIN_USERS.HASHED_PASSWORD, adminUser.getHashedPassword())
      .onConflict(ADMIN_USERS.USERNAME)
      .doUpdate()
      .set(ADMIN_USERS.HASHED_PASSWORD, adminUser.getHashedPassword())
      .execute()

    return adminUser
  }
}
