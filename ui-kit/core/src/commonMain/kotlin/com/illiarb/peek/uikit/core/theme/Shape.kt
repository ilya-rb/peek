package com.illiarb.peek.uikit.core.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val corneredShapeRadius = 24.dp

internal val UiKitShapes = Shapes(
  extraSmall = CircleShape,
  small = CircleShape,
  medium = RoundedCornerShape(corneredShapeRadius),
  large = RoundedCornerShape(corneredShapeRadius),
  extraLarge = RoundedCornerShape(corneredShapeRadius),
)
