package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.internal.RowCellInternal
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.common_action_cancel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
public fun NumberPickerCell(
  modifier: Modifier = Modifier,
  text: String,
  subtitle: String? = null,
  value: Int,
  options: ImmutableList<Int>,
  onValueSelected: (Int) -> Unit,
) {
  var showDialog by remember { mutableStateOf(false) }

  RowCellInternal(
    modifier = modifier.clickable { showDialog = true },
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
    startIcon = {},
    endContent = {},
  )

  if (showDialog) {
    NumberPickerDialog(
      title = text,
      currentValue = value,
      options = options,
      onDismiss = {
        showDialog = false
      },
      onSelected = { option ->
        onValueSelected(option)
        showDialog = false
      }
    )
  }
}

@Composable
private fun NumberPickerDialog(
  title: String,
  currentValue: Int,
  options: ImmutableList<Int>,
  onDismiss: () -> Unit,
  onSelected: (Int) -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = title) },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
          TextButton(
            onClick = { onSelected(option) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          ) {
            Text(
              text = option.toString(),
              style = MaterialTheme.typography.bodyLarge,
              color = if (option == currentValue) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.onSurface
              },
            )
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(Res.string.common_action_cancel))
      }
    },
  )
}
