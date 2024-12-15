package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_switch_checked
import org.jetbrains.compose.resources.stringResource

@Composable
public fun SwitchCell(
  title: String,
  subtitle: String,
  checked: Boolean,
  modifier: Modifier = Modifier,
  onChecked: (Boolean) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.padding(horizontal = 16.dp)
  ) {
    Column {
      Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    Spacer(Modifier.weight(1f))

    Switch(
      checked = checked,
      onCheckedChange = onChecked,
      thumbContent = {
        if (checked) {
          Icon(
            modifier = Modifier.size(SwitchDefaults.IconSize),
            imageVector = Icons.Filled.Check,
            contentDescription = stringResource(Res.string.acsb_switch_checked),
          )
        }
      }
    )
  }
}