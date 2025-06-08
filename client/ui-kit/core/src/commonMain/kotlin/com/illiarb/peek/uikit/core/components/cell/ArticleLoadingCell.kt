package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.shimmer.ShimmerBox
import com.valentinilk.shimmer.shimmer

@Composable
public fun ArticleLoadingCell() {
  Row(
    modifier = Modifier.shimmer(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      ShimmerBox(
        modifier = Modifier
          .padding(start = 16.dp, top = 16.dp)
          .size(width = 140.dp, height = 16.dp)
      )
      ShimmerBox(
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp, top = 12.dp)
          .size(width = 200.dp, height = 12.dp)
      )
      ShimmerBox(
        modifier = Modifier
          .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
          .size(width = 80.dp, height = 10.dp)
      )
    }

    Column(verticalArrangement = Arrangement.SpaceAround) {
      ShimmerBox(
        Modifier
          .padding(end = 16.dp)
          .padding(top = 8.dp)
          .size(24.dp)
      )
      ShimmerBox(
        Modifier
          .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
          .size(24.dp)
      )
    }
  }
}