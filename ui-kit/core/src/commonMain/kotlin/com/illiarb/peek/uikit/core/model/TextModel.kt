package com.illiarb.peek.uikit.core.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration

public data class TextModel(
  val text: String,
  val decoration: TextDecoration? = null,
  val color: Color? = null,
)
