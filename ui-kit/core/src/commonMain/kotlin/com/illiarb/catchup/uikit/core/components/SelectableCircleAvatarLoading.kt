package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
public fun SelectableCircleAvatarLoading(
  modifier: Modifier = Modifier,
  selected: Boolean,
) {
  val scale = if (selected) 1f else 0.8f

  Box(
    modifier = modifier
      .shimmer()
      .size(48.dp)
      .graphicsLayer(scaleX = scale, scaleY = scale)
      .clip(CircleShape)
      .background(Color.LightGray)
  )
}
