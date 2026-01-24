package com.illiarb.peek.uikit.core.image

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.illiarb.peek.uikit.core.model.IconStyle

@Immutable
public data class VectorIcon(
  val imageVector: ImageVector,
  val contentDescription: String,
  val tint: Color? = null,
  val style: IconStyle = IconStyle.Default,
)
