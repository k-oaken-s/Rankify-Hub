package rankifyHub.tier.domain.vo

/** 順序を表す値オブジェクト。 */
data class OrderIndex(val value: Int = 0) {
  init {
    require(value >= 0) { "Order Index must be greater than or equal to zero" }
  }
}
