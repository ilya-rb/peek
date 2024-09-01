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
    val primary = Color(0xFF00214A)
    val onPrimary = Color(0xFFFFFFFF)
    val primaryContainer = Color(0xFF234373)
    val onPrimaryContainer = Color(0xFFFFFFFF)
    val secondary = Color(0xFF192232)
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFF3A4354)
    val onSecondaryContainer = Color(0xFFFFFFFF)
    val tertiary = Color(0xFF301A35)
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFF523A58)
    val onTertiaryContainer = Color(0xFFFFFFFF)
    val error = Color(0xFF4E0002)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFF8C0009)
    val onErrorContainer = Color(0xFFFFFFFF)
    val background = Color(0xFFF9F9FF)
    val onBackground = Color(0xFF191C20)
    val surface = Color(0xFFF9F9FF)
    val onSurface = Color(0xFF000000)
    val surfaceVariant = Color(0xFFE0E2EC)
    val onSurfaceVariant = Color(0xFF21242B)
    val outline = Color(0xFF40434A)
    val outlineVariant = Color(0xFF40434A)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFF2E3036)
    val inverseOnSurface = Color(0xFFFFFFFF)
    val inversePrimary = Color(0xFFE5ECFF)
    val surfaceDim = Color(0xFFD9D9E0)
    val surfaceBright = Color(0xFFF9F9FF)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF3F3FA)
    val surfaceContainer = Color(0xFFEDEDF4)
    val surfaceContainerHigh = Color(0xFFE7E8EE)
    val surfaceContainerHighest = Color(0xFFE2E2E9)
  }

  private object Dark {
    val background = Color(0xFF111318)
    val errorContainer = Color(0xFFFFBAB1)
    val error = Color(0xFFFFF9F9)
    val inverseOnSurface = Color(0xFF000000)
    val inversePrimary = Color(0xFF002959)
    val inverseSurface = Color(0xFFE2E2E9)
    val onBackground = Color(0xFFE2E2E9)
    val onErrorContainer = Color(0xFF000000)
    val onError = Color(0xFF000000)
    val onPrimaryContainer = Color(0xFF000000)
    val onPrimary = Color(0xFF000000)
    val onSecondaryContainer = Color(0xFF000000)
    val onSecondary = Color(0xFF000000)
    val onSurface = Color(0xFFFFFFFF)
    val onSurfaceVariant = Color(0xFFFBFAFF)
    val onTertiaryContainer = Color(0xFF000000)
    val onTertiary = Color(0xFF000000)
    val outline = Color(0xFFC8CAD4)
    val outlineVariant = Color(0xFFC8CAD4)
    val primaryContainer = Color(0xFFB1CBFF)
    val primary = Color(0xFFFBFAFF)
    val scrim = Color(0xFF000000)
    val secondaryContainer = Color(0xFFC2CBE0)
    val secondary = Color(0xFFFBFAFF)
    val surfaceBright = Color(0xFF37393E)
    val surfaceContainer = Color(0xFF1D2024)
    val surfaceContainerHigh = Color(0xFF282A2F)
    val surfaceContainerHighest = Color(0xFF33353A)
    val surfaceContainerLow = Color(0xFF191C20)
    val surfaceContainerLowest = Color(0xFF0C0E13)
    val surface = Color(0xFF111318)
    val surfaceDim = Color(0xFF111318)
    val surfaceVariant = Color(0xFF44474E)
    val tertiaryContainer = Color(0xFFE1C0E5)
    val tertiary = Color(0xFFFFF9FA)
  }
}
