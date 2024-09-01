package com.illiarb.catchup.uikit.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import catchup_mobile.ui_kit.core.generated.resources.Res
import catchup_mobile.ui_kit.core.generated.resources.montserrat_bold
import catchup_mobile.ui_kit.core.generated.resources.montserrat_light
import catchup_mobile.ui_kit.core.generated.resources.montserrat_medium
import catchup_mobile.ui_kit.core.generated.resources.montserrat_normal
import catchup_mobile.ui_kit.core.generated.resources.montserrat_semibold
import org.jetbrains.compose.resources.Font

internal val UiKitTypography: Typography
  @Composable get() {
    val fontFamily = FontFamily(
      Font(Res.font.montserrat_light, FontWeight.Light),
      Font(Res.font.montserrat_normal, FontWeight.Normal),
      Font(Res.font.montserrat_medium, FontWeight.Medium),
      Font(Res.font.montserrat_semibold, FontWeight.SemiBold),
      Font(Res.font.montserrat_bold, FontWeight.Bold),
    )

    // Default Material 3 typography values
    val baseline = Typography()

    return Typography(
      bodyLarge = baseline.bodyLarge.copy(fontFamily = fontFamily),
      bodyMedium = baseline.bodyMedium.copy(fontFamily = fontFamily),
      bodySmall = baseline.bodySmall.copy(fontFamily = fontFamily),
      displayLarge = baseline.displayLarge.copy(fontFamily = fontFamily),
      displayMedium = baseline.displayMedium.copy(fontFamily = fontFamily),
      displaySmall = baseline.displaySmall.copy(fontFamily = fontFamily),
      headlineLarge = baseline.headlineLarge.copy(fontFamily = fontFamily),
      headlineMedium = baseline.headlineMedium.copy(fontFamily = fontFamily),
      headlineSmall = baseline.headlineSmall.copy(fontFamily = fontFamily),
      labelLarge = baseline.labelLarge.copy(fontFamily = fontFamily),
      labelMedium = baseline.labelMedium.copy(fontFamily = fontFamily),
      labelSmall = baseline.labelSmall.copy(fontFamily = fontFamily),
      titleLarge = baseline.titleLarge.copy(fontFamily = fontFamily),
      titleMedium = baseline.titleMedium.copy(fontFamily = fontFamily),
      titleSmall = baseline.titleSmall.copy(fontFamily = fontFamily),
    )
  }
