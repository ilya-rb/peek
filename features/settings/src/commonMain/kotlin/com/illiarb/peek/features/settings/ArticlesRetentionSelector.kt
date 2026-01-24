package com.illiarb.peek.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.BoxListItemContainer
import com.illiarb.peek.uikit.core.components.cell.ListHeader
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
      ListHeader(
        title = { style, modifier ->
          Text(title, modifier, style = style)
        }
      )

      LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
        items(
          count = options.size,
          key = { index -> options[index] },
          itemContent = { index ->
            val option = options[index]

            BoxListItemContainer(
              index = index,
              itemsCount = options.size,
              modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
              TextButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
                  coroutineScope.launch {
                    sheetState.hide()
                    onSelected(option)
                  }
                },
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
          },
        )
      }
    }
  }
}
