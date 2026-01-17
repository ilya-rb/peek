package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.model.VectorIcon
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun SwipeToDeleteContainer(
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  deleteIcon: VectorIcon = VectorIcon(
    Icons.Filled.Delete,
    contentDescription = "",
  ),
  content: @Composable RowScope.() -> Unit,
) {
  var offset by remember { mutableStateOf(0.dp) }
  var width by remember { mutableStateOf(0.dp) }
  var hasTriggeredHaptic by remember { mutableStateOf(false) }

  val hapticFeedback = LocalHapticFeedback.current
  val dismissState = rememberSwipeToDismissBoxState(
    positionalThreshold = { width.value / 2f }
  )

  LaunchedEffect(dismissState, width) {
    snapshotFlow { dismissState.requireOffset() }.collect { offsetPx ->
      val newOffset = abs(offsetPx).dp
      offset = newOffset

      val distanceToTriggerDismiss = width / 2f
      if (width > 0.dp && newOffset >= distanceToTriggerDismiss && !hasTriggeredHaptic) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
        hasTriggeredHaptic = true
      } else if (newOffset < distanceToTriggerDismiss) {
        hasTriggeredHaptic = false
      }
    }
  }

  SwipeToDismissBox(
    state = dismissState,
    modifier = modifier.onSizeChanged { size -> width = size.width.dp },
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = enabled,
    onDismiss = { onDelete() },
    backgroundContent = {
      DeleteBackground(
        modifier = modifier,
        offset = offset,
        width = width,
        deleteIcon = deleteIcon,
        shouldBounce = hasTriggeredHaptic,
      )
    },
    content = content,
  )
}

@Composable
private fun DeleteBackground(
  modifier: Modifier = Modifier,
  offset: Dp,
  width: Dp,
  deleteIcon: VectorIcon,
  shouldBounce: Boolean,
) {
  val errorColor = MaterialTheme.colorScheme.error
  val onErrorColor = MaterialTheme.colorScheme.onError
  val targetOffset = 200.dp
  val fraction = (offset / targetOffset).coerceIn(0f, 1f)

  val backgroundColor = lerp(
    start = Color.Transparent,
    stop = errorColor,
    fraction = fraction,
  )

  val iconColor = lerp(
    start = Color.Transparent,
    stop = onErrorColor,
    fraction = fraction,
  )

  var bounceTarget by remember { mutableStateOf(1f) }
  var rotation by remember { mutableStateOf(0f) }

  LaunchedEffect(shouldBounce) {
    if (shouldBounce) {
      // Scale up to 1.2f
      bounceTarget = 1.2f
      rotation = 20f
      // Wait for the spring animation to reach peak, then bounce back
      delay(200)
      // Scale back down to 1f (spring will naturally bounce)
      bounceTarget = 1f
      rotation = 0f
    } else {
      bounceTarget = 1f
      rotation = 0f
    }
  }

  val rotationBounce by animateFloatAsState(
    targetValue = rotation,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow,
    ),
    label = "rotation",
  )

  val bounceScale by animateFloatAsState(
    targetValue = bounceTarget,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow,
    ),
    label = "bounce",
  )

  Box(
    contentAlignment = Alignment.CenterEnd,
    modifier = Modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(16.dp))
      .background(backgroundColor)
  ) {
    Icon(
      modifier = Modifier
        .padding(end = 16.dp)
        .scale(bounceScale)
        .rotate(rotationBounce),
      imageVector = deleteIcon.imageVector,
      contentDescription = deleteIcon.contentDescription,
      tint = iconColor,
    )
  }
}
