package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun CheckableRow(
  title: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
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
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .defaultMinSize(minHeight = 56.dp)
      .clickable(enabled = enabled) { onCheckedChange(!checked) }
      .padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = null,
      enabled = enabled,
    )

    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
      Text(
        text = title,
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
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }

    if (trailingContent != null) {
      trailingContent()
    }
  }
}

@Preview
@Composable
private fun CheckableRowPreview() {
  PreviewTheme(darkMode = false) {
    CheckableRow(
      title = "Sample item text",
      checked = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowPreviewDark() {
  PreviewTheme(darkMode = true) {
    CheckableRow(
      title = "Sample item text",
      checked = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowCheckedPreview() {
  PreviewTheme(darkMode = false) {
    CheckableRow(
      title = "Completed item",
      checked = true,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowCheckedPreviewDark() {
  PreviewTheme(darkMode = true) {
    CheckableRow(
      title = "Completed item",
      checked = true,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowWithSubtitlePreview() {
  PreviewTheme(darkMode = false) {
    CheckableRow(
      title = "Item with subtitle",
      subtitle = "Additional information",
      checked = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowWithSubtitlePreviewDark() {
  PreviewTheme(darkMode = true) {
    CheckableRow(
      title = "Item with subtitle",
      subtitle = "Additional information",
      checked = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowDisabledPreview() {
  PreviewTheme(darkMode = false) {
    CheckableRow(
      title = "Disabled item",
      checked = false,
      enabled = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowDisabledPreviewDark() {
  PreviewTheme(darkMode = true) {
    CheckableRow(
      title = "Disabled item",
      checked = false,
      enabled = false,
      onCheckedChange = {}
    )
  }
}

@Preview
@Composable
private fun CheckableRowTrailingIconDark() {
  PreviewTheme(darkMode = true) {
    CheckableRow(
      title = "Disabled item",
      subtitle = "Subtitle",
      checked = true,
      enabled = false,
      onCheckedChange = {},
      trailingContent = {
        Icon(
          Icons.Filled.AssignmentLate,
          contentDescription = "",
          tint = MaterialTheme.colorScheme.error,
        )
      },
    )
  }
}

@Preview
@Composable
private fun CheckableRowTrailingIconLight() {
  PreviewTheme(darkMode = false) {
    CheckableRow(
      title = "Disabled item",
      subtitle = "Subtitle",
      checked = true,
      enabled = true,
      onCheckedChange = {},
      trailingContent = {
        Icon(
          Icons.Filled.AssignmentLate,
          contentDescription = "",
          tint = MaterialTheme.colorScheme.error,
        )
      },
    )
  }
}
