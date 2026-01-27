package com.illiarb.peek.core.types

public data class Currency(
  val code: String,
)

public object Currencies {
  public val USD: Currency = Currency("USD")
}
