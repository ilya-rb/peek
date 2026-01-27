package com.illiarb.peek.core.types

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

public data class Money(
  val amount: Double,
  val currency: Currency,
) {

  public fun format(): String {
    if (amount == 0.0) {
      return "0 ${currency.code}"
    }

    val magnitude = floor(log10(abs(amount))).toInt()
    val decimalPlaces = (SIGNIFICANT_DIGITS - 1 - magnitude).coerceAtLeast(0)
    val factor = 10.0.pow(decimalPlaces)
    val rounded = (amount * factor).roundToLong().toDouble() / factor

    val intPart = rounded.toLong()
    val fracPart = ((rounded - intPart) * factor).roundToLong()

    return if (decimalPlaces == 0) {
      "$intPart ${currency.code}"
    } else {
      "$intPart.${fracPart.toString().padStart(decimalPlaces, '0')} ${currency.code}"
    }
  }

  private companion object {
    const val SIGNIFICANT_DIGITS = 4
  }
}
