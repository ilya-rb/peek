package com.illiarb.peek.uikit.core.model

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public sealed interface IconStyle {
  public data object Default : IconStyle
  public data object CircleBackground : IconStyle
}

@Composable
internal fun IconStyle.asContainerModifier(): Modifier {
  return when (this) {
    IconStyle.Default -> Modifier
    IconStyle.CircleBackground -> {
      Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainer)
    }
  }
}

internal fun IconStyle.contentPadding(): Dp {
  return when (this) {
    IconStyle.Default -> 0.dp
    IconStyle.CircleBackground -> 8.dp
  }
}
