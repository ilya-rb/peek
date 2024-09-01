package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ArticleLoadingCell() {
  Column(modifier = Modifier.shimmer()) {
    val shimmerColor = Color.LightGray
    val shimmerCornerRadius = 8.dp

    Box(
      modifier = Modifier
        .padding(start = 16.dp, top = 16.dp)
        .size(width = 100.dp, height = 24.dp)
        .clip(RoundedCornerShape(shimmerCornerRadius))
        .background(shimmerColor)
    )
    Box(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        .size(width = 250.dp, height = 24.dp)
        .clip(RoundedCornerShape(shimmerCornerRadius))
        .background(shimmerColor)
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      Spacer(Modifier.weight(1f))

      Box(
        Modifier
          .padding(end = 16.dp).padding(top = 8.dp)
          .size(24.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
      Box(
        Modifier
          .padding(end = 16.dp).padding(top = 8.dp)
          .size(24.dp)
          .clip(RoundedCornerShape(shimmerCornerRadius))
          .background(shimmerColor)
      )
    }
  }
}
