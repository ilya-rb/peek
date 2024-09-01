package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.imageloader.UrlImage

@Composable
fun SelectableCircleAvatar(
  modifier: Modifier = Modifier,
  imageUrl: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  UrlImage(
    url = imageUrl,
    contentScale = ContentScale.Inside,
    modifier = modifier
      .size(60.dp)
      .clip(MaterialTheme.shapes.small)
      .alpha(
        if (selected) {
          1f
        } else {
          0.5f
        }
      )
      .clickable { onClick() }
  )
}
