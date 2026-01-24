package com.illiarb.peek.uikit.core.model

import androidx.compose.runtime.Immutable

@Immutable
public data class ButtonModel(
  val text: String,
  val enabled: Boolean = true,
  val onClick: () -> Unit,
)
