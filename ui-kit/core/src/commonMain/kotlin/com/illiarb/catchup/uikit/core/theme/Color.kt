package com.illiarb.catchup.uikit.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal expect fun colorScheme(useDarkTheme: Boolean, useDynamicColors: Boolean): ColorScheme

/**
 * Generated with
 * https://material-foundation.github.io/material-theme-builder/
 */
internal object UiKitColor {

  val colorSchemeLight: ColorScheme = lightColorScheme(
    background = Light.background,
    error = Light.error,
    errorContainer = Light.errorContainer,
    inverseOnSurface = Light.inverseOnSurface,
    inversePrimary = Light.inversePrimary,
    inverseSurface = Light.inverseSurface,
    onBackground = Light.onBackground,
    onError = Light.onError,
    onErrorContainer = Light.onErrorContainer,
    onPrimary = Light.onPrimary,
    onPrimaryContainer = Light.onPrimaryContainer,
    onSecondary = Light.onSecondary,
    onSecondaryContainer = Light.onSecondaryContainer,
    onSurface = Light.onSurface,
    onSurfaceVariant = Light.onSurfaceVariant,
    onTertiary = Light.onTertiary,
    onTertiaryContainer = Light.onTertiaryContainer,
    outline = Light.outline,
    outlineVariant = Light.outlineVariant,
    primary = Light.primary,
    primaryContainer = Light.primaryContainer,
    scrim = Light.scrim,
    secondary = Light.secondary,
    secondaryContainer = Light.secondaryContainer,
    surface = Light.surface,
    surfaceBright = Light.surfaceBright,
    surfaceContainer = Light.surfaceContainer,
    surfaceContainerHigh = Light.surfaceContainerHigh,
    surfaceContainerHighest = Light.surfaceContainerHighest,
    surfaceContainerLow = Light.surfaceContainerLow,
    surfaceContainerLowest = Light.surfaceContainerLowest,
    surfaceDim = Light.surfaceDim,
    surfaceVariant = Light.surfaceVariant,
    tertiary = Light.tertiary,
    tertiaryContainer = Light.tertiaryContainer,
  )

  val colorSchemeDark: ColorScheme = darkColorScheme(
    background = Dark.background,
    error = Dark.error,
    errorContainer = Dark.errorContainer,
    inverseOnSurface = Dark.inverseOnSurface,
    inversePrimary = Dark.inversePrimary,
    inverseSurface = Dark.inverseSurface,
    onBackground = Dark.onBackground,
    onError = Dark.onError,
    onErrorContainer = Dark.onErrorContainer,
    onPrimary = Dark.onPrimary,
    onPrimaryContainer = Dark.onPrimaryContainer,
    onSecondary = Dark.onSecondary,
    onSecondaryContainer = Dark.onSecondaryContainer,
    onSurface = Dark.onSurface,
    onSurfaceVariant = Dark.onSurfaceVariant,
    onTertiary = Dark.onTertiary,
    onTertiaryContainer = Dark.onTertiaryContainer,
    outline = Dark.outline,
    outlineVariant = Dark.outlineVariant,
    primary = Dark.primary,
    primaryContainer = Dark.primaryContainer,
    scrim = Dark.scrim,
    secondary = Dark.secondary,
    secondaryContainer = Dark.secondaryContainer,
    surface = Dark.surface,
    surfaceBright = Dark.surfaceBright,
    surfaceContainer = Dark.surfaceContainer,
    surfaceContainerHigh = Dark.surfaceContainerHigh,
    surfaceContainerHighest = Dark.surfaceContainerHighest,
    surfaceContainerLow = Dark.surfaceContainerLow,
    surfaceContainerLowest = Dark.surfaceContainerLowest,
    surfaceDim = Dark.surfaceDim,
    surfaceVariant = Dark.surfaceVariant,
    tertiary = Dark.tertiary,
    tertiaryContainer = Dark.tertiaryContainer,
  )

  private object Light {
    val primary = Color(0xFF68548E)
    val onPrimary = Color(0xFFFFFFFF)
    val primaryContainer = Color(0xFFEBDDFF)
    val onPrimaryContainer = Color(0xFF230F46)
    val secondary = Color(0xFF635B70)
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFFE9DEF8)
    val onSecondaryContainer = Color(0xFF1F182B)
    val tertiary = Color(0xFF7E525D)
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFFFFD9E1)
    val onTertiaryContainer = Color(0xFF31101B)
    val error = Color(0xFFBA1A1A)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF410002)
    val background = Color(0xFFFEF7FF)
    val onBackground = Color(0xFF1D1B20)
    val surface = Color(0xFFFEF7FF)
    val onSurface = Color(0xFF1D1B20)
    val surfaceVariant = Color(0xFFE7E0EB)
    val onSurfaceVariant = Color(0xFF49454E)
    val outline = Color(0xFF7A757F)
    val outlineVariant = Color(0xFFCBC4CF)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFF322F35)
    val inverseOnSurface = Color(0xFFF5EFF7)
    val inversePrimary = Color(0xFFD3BCFD)
    val surfaceDim = Color(0xFFDED8E0)
    val surfaceBright = Color(0xFFFEF7FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF8F1FA)
    val surfaceContainer = Color(0xFFF2ECF4)
    val surfaceContainerHigh = Color(0xFFEDE6EE)
    val surfaceContainerHighest = Color(0xFFE7E0E8)
  }

  private object Dark {
    val primary = Color(0xFFD3BCFD)
    val onPrimary = Color(0xFF38265C)
    val primaryContainer = Color(0xFF4F3D74)
    val onPrimaryContainer = Color(0xFFEBDDFF)
    val secondary = Color(0xFFCDC2DB)
    val onSecondary = Color(0xFF342D40)
    val secondaryContainer = Color(0xFF4B4358)
    val onSecondaryContainer = Color(0xFFE9DEF8)
    val tertiary = Color(0xFFF0B7C5)
    val onTertiary = Color(0xFF4A2530)
    val tertiaryContainer = Color(0xFF643B46)
    val onTertiaryContainer = Color(0xFFFFD9E1)
    val error = Color(0xFFFFB4AB)
    val onError = Color(0xFF690005)
    val errorContainer = Color(0xFF93000A)
    val onErrorContainer = Color(0xFFFFDAD6)
    val background = Color(0xFF151218)
    val onBackground = Color(0xFFE7E0E8)
    val surface = Color(0xFF151218)
    val onSurface = Color(0xFFE7E0E8)
    val surfaceVariant = Color(0xFF49454E)
    val onSurfaceVariant = Color(0xFFCBC4CF)
    val outline = Color(0xFF948F99)
    val outlineVariant = Color(0xFF49454E)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFFE7E0E8)
    val inverseOnSurface = Color(0xFF322F35)
    val inversePrimary = Color(0xFF68548E)
    val surfaceDim = Color(0xFF151218)
    val surfaceBright = Color(0xFF3B383E)
    val surfaceContainerLowest = Color(0xFF0F0D13)
    val surfaceContainerLow = Color(0xFF1D1B20)
    val surfaceContainer = Color(0xFF211F24)
    val surfaceContainerHigh = Color(0xFF2C292F)
    val surfaceContainerHighest = Color(0xFF36343A)
  }
}
