package com.illiarb.peek.core.types

import com.illiarb.peek.core.types.Currencies.USD
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatTest {

  @Test
  fun `it should format zero with no decimal places`() {
    assertEquals("0 USD", Money.fromDouble(0.0, USD).amountFormatted)
  }

  @Test
  fun `it should format with preserving 2 significant digits`() {
    assertEquals("0.00017 USD", Money.fromDouble(0.000168, USD).amountFormatted)
  }

  @Test
  fun `it should format value with significant integer and fractional parts`() {
    assertEquals("1.2 USD", Money.fromDouble(1.2345678, USD).amountFormatted)
  }

  @Test
  fun `it should format large value with no decimal places`() {
    assertEquals("123 USD", Money.fromDouble(123.456, USD).amountFormatted)
  }

  @Test
  fun `it should round up to significant digits`() {
    assertEquals("0.0017 USD", Money.fromDouble(0.0016789, USD).amountFormatted)
  }

  @Test
  fun `it should remove trailing zeroes`() {
    assertEquals("0.50 USD", Money.fromDouble(0.5, USD).amountFormatted)
  }
}
