package rankifyHub.admin.domain.model

import java.util.*

/**
 * 管理者エンティティ
 *
 * 管理者の認証情報を表すデータクラスで、データベース内の `administrator` テーブルにマッピングされています。
 */
data class Administrator(
  val id: String = UUID.randomUUID().toString(),
  val username: String,
  val passwordHash: String
)
