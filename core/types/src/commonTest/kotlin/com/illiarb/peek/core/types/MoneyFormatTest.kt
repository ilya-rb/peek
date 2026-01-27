package com.illiarb.peek.core.types

import com.illiarb.peek.core.types.Currencies.USD
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatTest {

  @Test
  fun `it should format zero as 0`() {
    assertEquals("0 USD", Money(0.0, USD).format())
  }

  @Test
  fun `it should format small value with leading zeros`() {
    assertEquals("0.0001680 USD", Money(0.000168, USD).format())
  }

  @Test
  fun `it should format very small value preserving 4 significant digits`() {
    assertEquals("0.00001234 USD", Money(0.00001234, USD).format())
  }

  @Test
  fun `it should format value with integer and fractional parts`() {
    assertEquals("1.235 USD", Money(1.2345678, USD).format())
  }

  @Test
  fun `it should format large value with 1 decimal place`() {
    assertEquals("123.5 USD", Money(123.456, USD).format())
  }

  @Test
  fun `it should format value with no decimal places needed`() {
    assertEquals("1235 USD", Money(1234.5678, USD).format())
  }

  @Test
  fun `it should round up correctly`() {
    assertEquals("0.001679 USD", Money(0.0016789, USD).format())
  }

  @Test
  fun `it should format exact value without trailing noise`() {
    assertEquals("0.5000 USD", Money(0.5, USD).format())
  }
}
