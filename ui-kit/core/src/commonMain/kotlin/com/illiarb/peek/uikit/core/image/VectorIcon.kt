package com.illiarb.peek.uikit.core.image

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
public data class VectorIcon(
  val imageVector: ImageVector,
  val contentDescription: String,
  val colorFilter: ColorFilter? = null,
)
