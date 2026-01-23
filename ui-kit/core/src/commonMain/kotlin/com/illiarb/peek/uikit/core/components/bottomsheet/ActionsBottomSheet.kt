package com.illiarb.peek.uikit.core.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import kotlinx.coroutines.launch

@Immutable
public data class ButtonModel(
  val text: String,
  val onClick: () -> Unit,
)

@Composable
public fun ActionsBottomSheet(
  title: String,
  description: String? = null,
  primaryButton: ButtonModel? = null,
  secondaryButton: ButtonModel? = null,
  content: @Composable () -> Unit = {},
  onDismiss: () -> Unit = {},
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  fun (() -> Unit).withSheetDismissal(): () -> Unit {
    return {
      coroutineScope.launch {
        sheetState.hide()
        invoke()
      }
    }
  }

  ModalBottomSheet(
    modifier = modifier,
    sheetState = sheetState,
    onDismissRequest = {
      coroutineScope.launch {
        sheetState.hide()
        onDismiss()
      }
    },
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .navigationBarsPadding()
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
      )

      if (description != null) {
        Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp),
        )
      }

      content()

      if (primaryButton != null || secondaryButton != null) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
        ) {
          if (secondaryButton != null) {
            OutlinedButton(
              modifier = Modifier.weight(1f),
              onClick = secondaryButton.onClick.withSheetDismissal(),
              content = { Text(secondaryButton.text) }
            )
          }
          if (primaryButton != null) {
            Button(
              modifier = Modifier.weight(1f),
              onClick = primaryButton.onClick.withSheetDismissal(),
              content = { Text(primaryButton.text) }
            )
          }
        }
      }
    }
  }
}

@Preview
@Composable
private fun ActionsBottomSheetPreview() {
  PreviewTheme(darkMode = false) {
    ActionsBottomSheet(
      title = "Delete item?",
      description = "This action cannot be undone.",
      primaryButton = ButtonModel("Delete") {},
    )
  }
}

@Preview
@Composable
private fun ActionsBottomSheetPreviewDark() {
  PreviewTheme(darkMode = true) {
    ActionsBottomSheet(
      title = "Delete item?",
      description = "This action cannot be undone.",
      primaryButton = ButtonModel("Delete") {},
    )
  }
}

@Preview
@Composable
private fun ActionsBottomSheetWithSecondaryPreview() {
  PreviewTheme(darkMode = false) {
    ActionsBottomSheet(
      title = "Uncheck task?",
      description = "This will mark the task as incomplete.",
      primaryButton = ButtonModel("Uncheck") {},
      secondaryButton = ButtonModel("Cancel") {},
    )
  }
}

@Preview
@Composable
private fun ActionsBottomSheetWithSecondaryPreviewDark() {
  PreviewTheme(darkMode = true) {
    ActionsBottomSheet(
      title = "Uncheck task?",
      description = "This will mark the task as incomplete.",
      primaryButton = ButtonModel("Uncheck") {},
      secondaryButton = ButtonModel("Cancel") {},
    )
  }
}
