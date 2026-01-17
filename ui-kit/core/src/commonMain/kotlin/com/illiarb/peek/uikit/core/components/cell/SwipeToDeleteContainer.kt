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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.model.VectorIcon
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun SwipeToDeleteContainer(
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  deleteIcon: VectorIcon = VectorIcon(
    imageVector = Icons.Filled.Delete,
    contentDescription = "",
  ),
  content: @Composable RowScope.() -> Unit,
) {
  var offsetPx by remember { mutableStateOf(0f) }
  var widthPx by remember { mutableStateOf(0) }
  var hapticTriggered by remember { mutableStateOf(false) }
  var distanceToTriggerDismiss by remember { mutableFloatStateOf(0f) }

  val hapticFeedback = LocalHapticFeedback.current
  val dismissState = rememberSwipeToDismissBoxState(
    positionalThreshold = { distanceToTriggerDismiss }
  )

  /**
   * Proper way is to utilise positionalThreshold as 0 and use
   * dismissState.progress instead of manual tracking but it looks like a bug
   * inside the component so positionalThreshold is always default (half of the width)
   */
  LaunchedEffect(dismissState) {
    snapshotFlow { dismissState.requireOffset() }.collect { offset ->
      val newOffset = abs(offset)
      offsetPx = newOffset

      if (widthPx > 0 && newOffset >= distanceToTriggerDismiss && !hapticTriggered) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
        hapticTriggered = true
      } else if (newOffset < distanceToTriggerDismiss) {
        hapticTriggered = false
      }
    }
  }

  SwipeToDismissBox(
    state = dismissState,
    modifier = modifier.onSizeChanged { size ->
      widthPx = size.width
      distanceToTriggerDismiss = widthPx / 2f
    },
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = enabled,
    onDismiss = { onDelete() },
    backgroundContent = {
      DeleteBackground(
        offset = offsetPx,
        distanceToTriggerDismiss = distanceToTriggerDismiss,
        deleteIcon = deleteIcon,
        shouldBounce = hapticTriggered,
      )
    },
    content = content,
  )
}

@Composable
private fun DeleteBackground(
  modifier: Modifier = Modifier,
  offset: Float,
  distanceToTriggerDismiss: Float,
  deleteIcon: VectorIcon,
  shouldBounce: Boolean,
) {
  val fraction = if (distanceToTriggerDismiss == 0f) {
    0f
  } else {
    (offset / distanceToTriggerDismiss).coerceIn(0f, 1f)
  }

  val backgroundColor = lerp(
    start = Color.Transparent,
    stop = MaterialTheme.colorScheme.error,
    fraction = fraction,
  )
  val iconColor = lerp(
    start = Color.Transparent,
    stop = MaterialTheme.colorScheme.onError,
    fraction = fraction,
  )
  val bounceScale by animateFloatAsState(
    targetValue = if (shouldBounce) 1.2f else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessLow,
    ),
  )

  Box(
    contentAlignment = Alignment.CenterEnd,
    modifier = modifier
      .fillMaxSize()
      .clip(RoundedCornerShape(16.dp))
      .background(backgroundColor)
  ) {
    Icon(
      imageVector = deleteIcon.imageVector,
      contentDescription = deleteIcon.contentDescription,
      tint = iconColor,
      modifier = Modifier
        .padding(end = 16.dp)
        .scale(bounceScale)
    )
  }
}
