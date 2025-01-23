package rankifyHub.tier.domain.vo

/**
 * 匿名ユーザー識別子を表す値オブジェクト。
 *
 * @property value 匿名IDの値
 */
data class AnonymousId(val value: String = "") {
  init {
    //    require(value.isNotBlank()) { "AnonymousId cannot be blank" }
    require(value.length <= 255) { "AnonymousId must be 255 characters or less" }
  }
}
