package com.illiarb.peek.core.types

public data class Money(
  val amount: Double,
  val currency: Currency,
) {

  public fun format(): String {
    val multiplied = (amount * MULTIPLIER).toLong()
    val integer = multiplied / MULTIPLIER.toLong()
    val decimal = (multiplied % MULTIPLIER.toLong()).toString().padStart(DECIMAL_PLACES, '0')

    return "$integer.$decimal ${currency.code}"
  }

  private companion object {
    const val DECIMAL_PLACES = 6
    const val MULTIPLIER = 1_000_000.0
  }
}
