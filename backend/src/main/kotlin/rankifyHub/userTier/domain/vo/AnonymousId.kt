package rankifyHub.userTier.domain.vo

/**
 * 匿名ユーザー識別子
 *
 * @property value 匿名IDの値
 */
data class AnonymousId(val value: String = "") {
  init {
    //    require(value.isNotBlank()) { "AnonymousId cannot be blank" }
    require(value.length <= 255) { "AnonymousId must be 255 characters or less" }
  }
}
