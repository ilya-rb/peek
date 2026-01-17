package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.shimmer.ShimmerBox
import com.illiarb.peek.uikit.core.components.shimmer.ShimmerCircle
import com.valentinilk.shimmer.shimmer

@Composable
public fun TaskLoadingCell() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(MaterialTheme.colorScheme.surfaceContainer)
      .shimmer(),
  ) {
    Row(
      modifier = Modifier
        .defaultMinSize(minHeight = 56.dp)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      ShimmerCircle(
        modifier = Modifier.size(20.dp),
      )

      ShimmerBox(
        modifier = Modifier
          .weight(1f)
          .padding(start = 12.dp)
          .height(16.dp),
      )
    }
  }
}
