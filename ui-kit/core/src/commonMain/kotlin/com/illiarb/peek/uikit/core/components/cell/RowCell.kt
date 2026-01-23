package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.cell.internal.RowCellInternal
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes

@Composable
public fun RowCell(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String? = null,
  startIcon: VectorIcon? = null,
  endActionText: String? = null,
  onEndActionClicked: () -> Unit = {},
) {
  RowCellInternal(
    modifier = modifier,
    title = {
      Text(text = title, style = MaterialTheme.typography.bodyLarge)
    },
    startIcon = {
      if (startIcon != null) {
        Icon(
          modifier = Modifier.padding(end = 16.dp),
          imageVector = startIcon.imageVector,
          contentDescription = startIcon.contentDescription,
        )
      }
    },
    subtitle = {
      if (subtitle != null) {
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
      }
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

@Preview
@Composable
private fun RowCellFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    RowCellFullPreview()
  }
}

@Preview
@Composable
private fun RowCellFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    RowCellFullPreview()
  }
}

@Composable
private fun RowCellFullPreview() {
  RowCell(
    title = "Settings",
    subtitle = "Configure your preferences",
    startIcon = VectorIcon(
      imageVector = Icons.Filled.Settings,
      contentDescription = "Settings icon"
    ),
    endActionText = "Edit",
    onEndActionClicked = {}
  )
}

@Preview
@Composable
private fun RowCellStatesPreview() {
  PreviewTheme(darkMode = false) {
    RowCellStatesPreviewContent()
  }
}

@Composable
private fun RowCellStatesPreviewContent() {
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    RowPreviewBox {
      RowCell(
        title = "Title only",
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle",
        subtitle = "Additional information"
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With icon",
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Icon"
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With action",
        endActionText = "Action",
        onEndActionClicked = {}
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle and icon",
        subtitle = "Subtitle text",
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Icon"
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle and action",
        subtitle = "Subtitle text",
        endActionText = "Action",
        onEndActionClicked = {}
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With icon and action",
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Settings,
          contentDescription = "Icon"
        ),
        endActionText = "Action",
        onEndActionClicked = {}
      )
    }
  }
}

@Composable
private fun RowPreviewBox(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceContainer,
    shape = UiKitShapes.medium,
  ) {
    content()
  }
}
