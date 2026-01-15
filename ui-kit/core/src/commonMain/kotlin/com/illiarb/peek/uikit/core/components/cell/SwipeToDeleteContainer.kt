package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun SwipeToDeleteContainer(
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  deleteIcon: ImageVector = Icons.Filled.Delete,
  deleteIconContentDescription: String? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val dismissState = rememberSwipeToDismissBoxState()

  LaunchedEffect(dismissState) {
    snapshotFlow { dismissState.currentValue }
      .filter { it == SwipeToDismissBoxValue.EndToStart }
      .collect { onDelete() }
  }

  SwipeToDismissBox(
    state = dismissState,
    modifier = modifier,
    enableDismissFromStartToEnd = false,
    enableDismissFromEndToStart = enabled,
    backgroundContent = {
      DeleteBackground(
        dismissInProgress = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart,
        deleteIcon = deleteIcon,
        deleteIconContentDescription = deleteIconContentDescription,
      )
    },
    content = content,
  )
}

@Composable
private fun DeleteBackground(
  dismissInProgress: Boolean,
  deleteIcon: ImageVector,
  deleteIconContentDescription: String?,
) {
  val backgroundColor by animateColorAsState(
    targetValue = if (dismissInProgress) {
      MaterialTheme.colorScheme.error
    } else {
      Color.Transparent
    },
    animationSpec = tween(durationMillis = 150),
  )

  val iconColor by animateColorAsState(
    targetValue = if (dismissInProgress) {
      MaterialTheme.colorScheme.onError
    } else {
      Color.Transparent
    },
    animationSpec = tween(durationMillis = 150),
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(backgroundColor)
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.CenterEnd,
  ) {
    Icon(
      imageVector = deleteIcon,
      contentDescription = deleteIconContentDescription,
      tint = iconColor,
    )
  }
}
