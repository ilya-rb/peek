package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun SelectableCircleAvatarLoading(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .shimmer()
      .size(60.dp)
      .clip(CircleShape)
      .background(Color.LightGray)
  )
}
