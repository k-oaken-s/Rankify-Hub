package rankifyHub.tier.domain.vo

data class OrderIndex(val value: Int = 0) {
  init {
    require(value >= 0) { "Order Index must be greater than or equal to zero" }
  }
}
