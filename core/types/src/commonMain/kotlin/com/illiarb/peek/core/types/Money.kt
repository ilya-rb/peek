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

      val amountPrecise = amount.toDouble() / PRECISION_FACTOR
      val magnitude = floor(log10(abs(amountPrecise))).toInt()
      val decimalPlaces = (SIGNIFICANT_DIGITS - 1 - magnitude).coerceAtLeast(0)
      val factor = 10.0.pow(decimalPlaces)
      val rounded = (amountPrecise * factor).roundToLong().toDouble() / factor

      val intPart = rounded.toLong()
      val fracPart = ((rounded - intPart) * factor).roundToLong()

      return if (decimalPlaces == 0) {
        "$intPart ${currency.code}"
      } else {
        "$intPart.${fracPart.toString().padStart(decimalPlaces, '0')} ${currency.code}"
      }
    }

  public companion object {
    private const val SIGNIFICANT_DIGITS = 4
    private const val PRECISION_FACTOR = 100_000_000L

    public fun fromDouble(amount: Double, currency: Currency): Money {
      return Money((amount * PRECISION_FACTOR).roundToLong(), currency)
    }
  }
}
