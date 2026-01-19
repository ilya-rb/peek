package com.illiarb.peek.uikit.core.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

public sealed interface BorderState {

  public data object None : BorderState

  public data class Static(val color: Color) : BorderState

  public data class Pulsating(val color: Color) : BorderState
}

@Composable
public fun BorderedIcon(
  borderState: BorderState,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val borderWidth = 2.dp
  val borderPadding = 4.dp
  val borderModifier = when (borderState) {
    is BorderState.None -> Modifier

    is BorderState.Static -> Modifier
      .border(
        width = borderWidth,
        color = borderState.color,
        shape = CircleShape,
      )
      .padding(borderPadding)

    is BorderState.Pulsating -> {
      val infiniteTransition = rememberInfiniteTransition()
      val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(durationMillis = 1000),
          repeatMode = RepeatMode.Reverse,
        ),
      )

      Modifier
        .border(
          width = borderWidth,
          color = borderState.color.copy(alpha = alpha),
          shape = CircleShape,
        )
        .padding(borderPadding)
    }
  }

  Box(modifier = modifier.then(borderModifier)) {
    content()
  }
}
