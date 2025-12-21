package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.internal.RowCellInternal

@Composable
public fun RowCell(
  modifier: Modifier = Modifier,
  text: String,
  startIcon: ImageVector,
  startIconContentDescription: String,
  endActionText: String? = null,
  onEndActionClicked: () -> Unit = {},
) {
  RowCellInternal(
    modifier = modifier,
    title = {
      Text(text = text, style = MaterialTheme.typography.bodyLarge)
    },
    startIconPadding = 16.dp,
    startIcon = {
      Icon(
        imageVector = startIcon,
        contentDescription = startIconContentDescription,
      )
    },
    subtitle = {
    },
    endContent = {
      if (endActionText != null) {
        TextButton(
          onClick = onEndActionClicked,
          content = { Text(text = endActionText) },
        )
      }
    }
  )
}
