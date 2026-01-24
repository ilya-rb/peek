package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.cell.internal.RowCellInternal
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_switch_checked
import org.jetbrains.compose.resources.stringResource

@Composable
public fun SwitchCell(
  modifier: Modifier = Modifier,
  text: String,
  subtitle: String? = null,
  startIcon: VectorIcon? = null,
  switchChecked: Boolean,
  onChecked: (Boolean) -> Unit,
) {
  RowCellInternal(
    modifier = modifier,
    title = {
      Text(text = text, style = MaterialTheme.typography.bodyLarge)
    },
    subtitle = {
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    startContent = { modifier ->
      if (startIcon != null) {
        Icon(
          modifier = modifier,
          imageVector = startIcon.imageVector,
          contentDescription = startIcon.contentDescription,
        )
      }
    },
    endContent = {
      Switch(
        checked = switchChecked,
        onCheckedChange = onChecked,
        thumbContent = {
          if (switchChecked) {
            Icon(
              modifier = Modifier.size(SwitchDefaults.IconSize),
              imageVector = Icons.Filled.Check,
              contentDescription = stringResource(Res.string.acsb_switch_checked),
            )
          }
        }
      )
    }
  )
}

@Preview
@Composable
private fun SwitchCellFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    SwitchCellFullPreview()
  }
}

@Preview
@Composable
private fun SwitchCellFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    SwitchCellFullPreview()
  }
}

@Composable
private fun SwitchCellFullPreview() {
  SwitchCell(
    text = "Enable notifications",
    subtitle = "Receive push notifications",
    startIcon = VectorIcon(
      imageVector = Icons.Filled.Check,
      contentDescription = "Icon"
    ),
    switchChecked = true,
    onChecked = {}
  )
}

@Preview
@Composable
private fun SwitchCellStatesPreview() {
  PreviewTheme(darkMode = false) {
    SwitchCellStatesPreviewContent()
  }
}

@Composable
private fun SwitchCellStatesPreviewContent() {
  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier.padding(16.dp)
  ) {
    SwitchPreviewBox {
      SwitchCell(
        text = "Title only",
        switchChecked = false,
        onChecked = {}
      )
    }

    SwitchPreviewBox {
      SwitchCell(
        text = "With subtitle",
        subtitle = "Additional information",
        switchChecked = true,
        onChecked = {}
      )
    }

    SwitchPreviewBox {
      SwitchCell(
        text = "With icon",
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Check,
          contentDescription = "Icon"
        ),
        switchChecked = false,
        onChecked = {}
      )
    }

    SwitchPreviewBox {
      SwitchCell(
        text = "With subtitle and icon",
        subtitle = "Subtitle text",
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Check,
          contentDescription = "Icon"
        ),
        switchChecked = true,
        onChecked = {}
      )
    }
  }
}

@Composable
private fun SwitchPreviewBox(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceContainer,
    shape = UiKitShapes.medium,
  ) {
    content()
  }
}
