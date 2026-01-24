package com.illiarb.peek.uikit.core.atom

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.theme.UiKitShapes

@Composable
public fun BoxListItemContainer(
  index: Int,
  itemsCount: Int,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val shape = when {
    itemsCount == 1 -> UiKitShapes.medium

    index == 0 -> UiKitShapes.medium.copy(
      bottomEnd = CornerSize(0.dp),
      bottomStart = CornerSize(0.dp)
    )

    index == itemsCount - 1 -> UiKitShapes.medium.copy(
      topEnd = CornerSize(0.dp),
      topStart = CornerSize(0.dp)
    )

    else -> RoundedCornerShape(0.dp)
  }

  Surface(
    modifier = modifier,
    shape = shape,
    color = MaterialTheme.colorScheme.surfaceContainer,
    content = content,
  )
}
