package com.illiarb.catchup.summarizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.ShimmerColumn
import com.illiarb.catchup.uikit.core.theme.UiKitTheme

@Composable
@Preview
internal fun SummaryLoadingPreview() {
  UiKitTheme(useDarkTheme = true, useDynamicColors = false) {
    ShimmerColumn {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Filled.Assistant,
          contentDescription = "AI"
        )
        Text(
          modifier = Modifier.padding(horizontal = 8.dp),
          text = "Summarizing...",
          style = MaterialTheme.typography.bodyLarge,
        )
      }
      Box(
        content = {},
        modifier = Modifier
          .padding(top = 16.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .size(width = 250.dp, height = 16.dp),
      )
      Box(
        content = {},
        modifier = Modifier
          .padding(top = 8.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .size(width = 150.dp, height = 16.dp),
      )
    }
  }
}