package com.illiarb.peek.uikit.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
public fun UiKitTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  useDynamicColors: Boolean,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = colorScheme(useDarkTheme, useDynamicColors),
    content = content,
    typography = UiKitTypography,
    shapes = UiKitShapes,
  )
}
