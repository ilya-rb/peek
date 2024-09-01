package com.illiarb.catchup.uikit.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun UiKitTheme(
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
