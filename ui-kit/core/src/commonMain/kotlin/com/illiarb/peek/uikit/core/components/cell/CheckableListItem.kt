package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
public fun CheckableListItem(
  text: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  trailingContent: @Composable (() -> Unit)? = null,
) {
  val textColor by animateColorAsState(
    targetValue = if (checked) {
      MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    } else {
      MaterialTheme.colorScheme.onSurface
    },
    animationSpec = tween(durationMillis = 150),
  )

  Row(
    modifier = modifier
      .defaultMinSize(minHeight = 56.dp)
      .clickable(enabled = enabled) { onCheckedChange(!checked) }
      .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = null,
      enabled = enabled,
    )

    Text(
      text = text,
      modifier = Modifier.weight(1f).padding(start = 12.dp),
      style = MaterialTheme.typography.bodyLarge,
      color = textColor,
      textDecoration = if (checked) {
        TextDecoration.LineThrough
      } else {
        TextDecoration.None
      },
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )

    if (trailingContent != null) {
      trailingContent()
    }
  }
}
