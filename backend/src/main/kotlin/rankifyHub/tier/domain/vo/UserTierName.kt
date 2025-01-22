package rankifyHub.tier.domain.vo

/**
 * ユーザーが作成したTierの名前を表す値オブジェクト
 *
 * @property value Tierの名前の値
 * @throws IllegalArgumentException 名前が無効な場合にスローされます
 */
data class UserTierName(val value: String = "") {
    init {
        //    require(value.isNotBlank()) { "Tierの名前は空白にできません。" }
        require(value.length <= 255) { "Tierの名前は255文字以下である必要があります。" }
    }

    override fun toString(): String = value
}
