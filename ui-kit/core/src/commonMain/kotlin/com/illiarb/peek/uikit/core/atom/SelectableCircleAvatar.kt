package com.illiarb.peek.uikit.core.atom

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.hn_logo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

public enum class AvatarState {
  Default,
  Selected,
  Unselected,
}

@Composable
public fun SelectableCircleAvatar(
  modifier: Modifier = Modifier,
  image: DrawableResource,
  state: AvatarState = AvatarState.Default,
  onClick: () -> Unit,
) {
  val animationSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow,
  )
  val alpha: Float by animateFloatAsState(
    targetValue = when (state) {
      AvatarState.Default, AvatarState.Selected -> 1f
      AvatarState.Unselected -> 0.5f
    },
    animationSpec = animationSpec,
  )
  val scale: Float by animateFloatAsState(
    targetValue = when (state) {
      AvatarState.Default, AvatarState.Selected -> 1f
      AvatarState.Unselected -> 0.8f
    },
    animationSpec = animationSpec,
  )
  val borderWidth = 2.dp

  Image(
    imageVector = vectorResource(image),
    contentDescription = null,
    contentScale = ContentScale.Fit,
    modifier = modifier
      .size(48.dp)
      .padding(borderWidth)
      .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
      .then(
        if (state == AvatarState.Selected) {
          Modifier.animatedSelectedBorder(borderWidth)
        } else {
          Modifier
        }
      )
      .clip(CircleShape)
      .clickable(onClick = onClick)
  )
}

@Composable
private fun Modifier.animatedSelectedBorder(borderWidth: Dp): Modifier {
  val colors = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.primaryContainer,
    MaterialTheme.colorScheme.onPrimary,
    MaterialTheme.colorScheme.primary,
  )
  val infiniteTransition = rememberInfiniteTransition()
  val angle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500),
      repeatMode = RepeatMode.Restart,
    ),
  )
  return this.selectedBorder(colors, borderWidth, angle)
}

private fun Modifier.selectedBorder(
  colors: List<Color>,
  strokeWidth: Dp,
  angle: Float,
) = drawWithCache {
  val brush = Brush.sweepGradient(colors)
  val radius = size.width / 2f
  val strokeWidthPx = strokeWidth.toPx()

  onDrawWithContent {
    drawContent()

    rotate(angle) {
      drawCircle(
        brush = brush,
        radius = radius,
        blendMode = BlendMode.SrcIn,
        style = Stroke(width = strokeWidthPx),
      )
    }
  }
}

@Preview
@Composable
private fun SelectableCircleAvatarFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    SelectableCircleAvatarFullPreview()
  }
}

@Preview
@Composable
private fun SelectableCircleAvatarFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    SelectableCircleAvatarFullPreview()
  }
}

@Composable
private fun SelectableCircleAvatarFullPreview() {
  SelectableCircleAvatar(
    image = Res.drawable.hn_logo,
    state = AvatarState.Selected,
    onClick = {}
  )
}

@Preview
@Composable
private fun SelectableCircleAvatarStatesPreview() {
  PreviewTheme(darkMode = false) {
    SelectableCircleAvatarStatesPreviewContent()
  }
}

@Composable
private fun SelectableCircleAvatarStatesPreviewContent() {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    SelectableCircleAvatar(
      image = Res.drawable.hn_logo,
      state = AvatarState.Default,
      onClick = {}
    )
    SelectableCircleAvatar(
      image = Res.drawable.hn_logo,
      state = AvatarState.Selected,
      onClick = {}
    )
    SelectableCircleAvatar(
      image = Res.drawable.hn_logo,
      state = AvatarState.Unselected,
      onClick = {}
    )
  }
}
