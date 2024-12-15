package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
public fun TopAppBarTitleLoading() {
  Box(
    Modifier
      .shimmer()
      .size(height = 16.dp, width = 40.dp)
      .clip(RoundedCornerShape(8.dp))
  )
}