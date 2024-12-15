package com.illiarb.catchup.uikit.core.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.painter.TextWithBackgroundPainter
import com.illiarb.catchup.uikit.imageloader.UrlImage

@Composable
public fun SelectableCircleAvatar(
  modifier: Modifier = Modifier,
  imageUrl: String,
  fallbackText: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  val colors = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.primaryContainer,
  )

  val alpha: Float by animateFloatAsState(if (selected) 1f else 0.5f)
  val scale: Float by animateFloatAsState(if (selected) 1f else 0.8f)
  val borderWidth = 2.dp
  val infiniteTransition = rememberInfiniteTransition()
  val angle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1500),
      repeatMode = RepeatMode.Restart,
    ),
  )
  val textMeasurer = rememberTextMeasurer()

  UrlImage(
    url = imageUrl,
    contentScale = ContentScale.Inside,
    error = TextWithBackgroundPainter(
      textMeasurer = textMeasurer,
      textStyle = MaterialTheme.typography.labelMedium,
      text = fallbackText.take(n = 2),
      backgroundColor = MaterialTheme.colorScheme.surfaceBright,
    ),
    modifier = modifier
      .size(48.dp)
      .padding(borderWidth)
      .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
      .let {
        if (selected) {
          it.selectedBorder(colors, borderWidth, angle)
        } else {
          it
        }
      }
      .clip(CircleShape)
      .clickable { onClick() }
  )
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
