package com.illiarb.catchup.uikit.core.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.components.FullscreenState

@Composable
@Preview(showBackground = true)
fun FullscreenStatePreview() {
  FullscreenState(
    title = "Title",
    buttonText = "Subtitle",
    onButtonClick = {},
  ) {
    Box(
      modifier = Modifier
          .size(150.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color.LightGray)
    )
  }
}
