package com.illiarb.peek.uikit.core.atom

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

public sealed interface BorderState {

  public data object None : BorderState

  public data class Static(val color: Color) : BorderState

  public data class Pulsating(val color: Color) : BorderState
}

@Composable
public fun Modifier.bordered(borderState: BorderState): Modifier {
  val width = 2.dp
  val padding = 4.dp

  val borderModifier = when (borderState) {
    is BorderState.None -> Modifier

    is BorderState.Static -> Modifier
      .border(
        width = width,
        color = borderState.color,
        shape = CircleShape,
      )
      .padding(padding)

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
          width = width,
          color = borderState.color.copy(alpha = alpha),
          shape = CircleShape,
        )
        .padding(padding)
    }
  }

  return this.then(borderModifier)
}

@Preview
@Composable
private fun BorderedIconNonePreview() {
  PreviewTheme(darkMode = false) {
    Icon(
      modifier = Modifier.bordered(BorderState.None),
      imageVector = Icons.Filled.Settings,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Preview
@Composable
private fun BorderedIconNonePreviewDark() {
  PreviewTheme(darkMode = true) {
    Icon(
      modifier = Modifier.bordered(BorderState.None),
      imageVector = Icons.Filled.Settings,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Preview
@Composable
private fun BorderedIconStaticPreview() {
  PreviewTheme(darkMode = false) {
    Icon(
      modifier = Modifier.bordered(BorderState.Static(MaterialTheme.colorScheme.primary)),
      imageVector = Icons.Filled.Settings,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Preview
@Composable
private fun BorderedIconStaticPreviewDark() {
  PreviewTheme(darkMode = true) {
    Icon(
      modifier = Modifier.bordered(BorderState.Static(MaterialTheme.colorScheme.primary)),
      imageVector = Icons.Filled.Settings,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurface
    )
  }
}
