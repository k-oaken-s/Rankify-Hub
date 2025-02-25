package rankifyHub.tier.domain.vo

import java.util.*

/**
 * アクセスURLを表す値オブジェクト。
 *
 * @property value アクセスURLの値
 */
data class AccessUrl(val value: String = "") {
  init {
    require(value.length <= 255) { "AccessUrl must be 255 characters or less" }
  }

  companion object {
    /** 新しいアクセスURLを生成 */
    fun generate(): AccessUrl {
      val uuid = UUID.randomUUID().toString()
      return AccessUrl(uuid)
    }
  }
}
