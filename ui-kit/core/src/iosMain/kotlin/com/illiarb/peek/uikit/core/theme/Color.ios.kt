package com.illiarb.peek.uikit.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun colorScheme(useDarkTheme: Boolean, useDynamicColors: Boolean): ColorScheme =
  when {
    useDarkTheme -> UiKitColorScheme.colorSchemeDark
    else -> UiKitColorScheme.colorSchemeLight
  }
