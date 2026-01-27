package com.illiarb.peek.core.types

public data class Currency(
  val code: String,
  val fractionalDigits: Int,
) {
  public companion object {
    public fun ofCode(code: String): Currency {
      return currencies[code] ?: throw IllegalArgumentException("Unsupported currency $code")
    }
  }
}

public data object Currencies {
  public val USD: Currency get() = Currency.ofCode("USD")
}

private val currencies = mapOf(
  "USD" to Currency(code = "USD", fractionalDigits = 2)
)
