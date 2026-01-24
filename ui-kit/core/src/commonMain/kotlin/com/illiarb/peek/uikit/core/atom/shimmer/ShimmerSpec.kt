package com.illiarb.peek.uikit.core.atom.shimmer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal object ShimmerSpec {

  @Composable
  fun color(): Color {
    return MaterialTheme.colorScheme.surfaceVariant
  }
}
