package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.internal.RowCellInternal
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.common_action_cancel
import com.illiarb.peek.uikit.resources.settings_article_retention_days_option
import com.illiarb.peek.uikit.resources.settings_article_retention_dialog_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
public fun NumberPickerCell(
  modifier: Modifier = Modifier,
  text: String,
  subtitle: String,
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
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary,
      )
    },
    startIcon = {},
    endContent = {},
  )

  if (showDialog) {
    NumberPickerBottomSheet(
      title = stringResource(Res.string.settings_article_retention_dialog_title),
      currentValue = value,
      options = options,
      onDismiss = { showDialog = false },
      onSelected = { option ->
        onValueSelected(option)
        showDialog = false
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NumberPickerBottomSheet(
  title: String,
  currentValue: Int,
  options: ImmutableList<Int>,
  onDismiss: () -> Unit,
  onSelected: (Int) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      )
      Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .clip(RoundedCornerShape(16.dp))
      ) {
        Column {
          options.forEach { option ->
            TextButton(
              onClick = { onSelected(option) },
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
              Text(
                text = stringResource(Res.string.settings_article_retention_days_option, option),
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
      }
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
      ) {
        Text(stringResource(Res.string.common_action_cancel))
      }
    }
  }
}
