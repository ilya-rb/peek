package com.illiarb.catchup.uikit.core.components.cell

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.shimmer.ShimmerCircle
import com.valentinilk.shimmer.shimmer

@Composable
public fun SelectableCircleAvatarLoading(
  modifier: Modifier = Modifier,
  selected: Boolean,
) {
  val scale = if (selected) 1f else 0.8f

  ShimmerCircle(
    modifier = modifier
      .shimmer()
      .size(48.dp)
      .graphicsLayer(scaleX = scale, scaleY = scale)
  )
}
