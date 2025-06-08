package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.shimmer.ShimmerBox
import com.valentinilk.shimmer.shimmer

@Composable
public fun TopAppBarTitleLoading() {
  ShimmerBox(
    Modifier
      .size(height = 16.dp, width = 80.dp)
      .shimmer()
  )
}