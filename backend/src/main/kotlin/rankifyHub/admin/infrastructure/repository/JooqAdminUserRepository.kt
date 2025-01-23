package rankifyHub.admin.infrastructure.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import rankifyHub.admin.domain.model.AdminUser
import rankifyHub.admin.domain.repository.AdminUserRepository
import rankifyHub.tables.AdminUsers.ADMIN_USERS

/** 管理者ユーザーのJOOQ永続化実装 */
@Repository
class JooqAdminUserRepository(private val dsl: DSLContext) : AdminUserRepository {

  /**
   * ユーザー名で管理者を検索
   *
   * @param username 検索対象のユーザー名
   */
  override fun findByUsername(username: String): AdminUser? {
    val record =
      dsl.selectFrom(ADMIN_USERS).where(ADMIN_USERS.USERNAME.eq(username)).fetchOne() ?: return null

    return AdminUser.reconstruct(
      id = record.get(ADMIN_USERS.ID),
      username = record.get(ADMIN_USERS.USERNAME),
      hashedPassword = record.get(ADMIN_USERS.HASHED_PASSWORD)
    )
  }

  /** 管理者を保存 既存ユーザーの場合はパスワードのみ更新 */
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
