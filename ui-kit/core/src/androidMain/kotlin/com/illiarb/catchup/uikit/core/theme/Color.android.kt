package com.illiarb.catchup.uikit.core.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun colorScheme(useDarkTheme: Boolean, useDynamicColors: Boolean): ColorScheme =
  when {
    Build.VERSION.SDK_INT >= 31 && useDynamicColors && useDarkTheme -> {
      dynamicDarkColorScheme(LocalContext.current)
    }

    Build.VERSION.SDK_INT >= 31 && useDynamicColors && !useDarkTheme -> {
      dynamicLightColorScheme(LocalContext.current)
    }

    useDarkTheme -> UiKitColor.colorSchemeDark

    else -> UiKitColor.colorSchemeLight
  }
