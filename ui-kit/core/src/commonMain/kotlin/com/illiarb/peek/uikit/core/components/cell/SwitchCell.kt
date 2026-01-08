package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.internal.RowCellInternal
import com.illiarb.peek.uikit.core.model.VectorIcon
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
    startIconPadding = if (startIcon == null) 0.dp else 16.dp,
    startIcon = {
      if (startIcon != null) {
        Icon(
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
