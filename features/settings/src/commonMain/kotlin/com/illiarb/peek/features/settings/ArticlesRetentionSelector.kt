package com.illiarb.peek.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.settings_article_retention_days_option
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ArticlesRetentionSelector(
  title: String,
  currentValue: Int,
  options: ImmutableList<Int>,
  onDismiss: () -> Unit,
  onSelected: (Int) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()

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
              onClick = {
                coroutineScope.launch {
                  sheetState.hide()
                  onSelected(option)
                }
              },
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
    }
  }
}
