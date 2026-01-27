package com.illiarb.peek.core.types

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

public data class Money(
  val amount: Long,
  val currency: Currency,
) {

  public val amountFormatted: String
    get() {
      if (amount == 0L) {
        return "0 ${currency.code}"
      }

      val amountPrecise = amount.toDouble().div(10.0.pow(MAX_PRECISION))
      val magnitude = floor(log10(abs(amountPrecise))).toInt()
      val decimalPlaces = (SIGNIFICANT_DIGITS - 1 - magnitude).coerceAtLeast(0)
      val factor = 10.0.pow(decimalPlaces)
      val rounded = (amountPrecise * factor).roundToLong().toDouble() / factor

      val integerPart = rounded.toLong()
      val fractionalPart = ((rounded - integerPart) * factor).roundToLong()

      return if (decimalPlaces == 0) {
        "$integerPart ${currency.code}"
      } else {
        "$integerPart.${fractionalPart.toString().padStart(decimalPlaces, '0')} ${currency.code}"
      }
    }

  public companion object {
    private const val SIGNIFICANT_DIGITS = 2
    private const val MAX_PRECISION = 8

    public fun fromDouble(amount: Double, currency: Currency): Money {
      return Money((amount * 10.0.pow(MAX_PRECISION)).roundToLong(), currency)
    }
  }
}
