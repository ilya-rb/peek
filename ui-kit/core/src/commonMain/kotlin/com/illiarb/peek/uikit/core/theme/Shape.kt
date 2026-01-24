package com.illiarb.peek.uikit.core.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

public val UiKitShapes: Shapes = Shapes(
  extraSmall = CircleShape,
  small = CircleShape,
  medium = RoundedCornerShape(24.dp),
  large = RoundedCornerShape(16.dp),
  extraLarge = RoundedCornerShape(16.dp),
)
