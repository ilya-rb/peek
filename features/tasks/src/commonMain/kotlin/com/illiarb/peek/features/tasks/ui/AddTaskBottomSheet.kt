package com.illiarb.peek.features.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.tasks_add_input_hint
import com.illiarb.peek.uikit.resources.tasks_add_submit
import com.illiarb.peek.uikit.resources.tasks_add_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AddTaskBottomSheet(
  onDismiss: () -> Unit,
  onSubmit: (String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val focusRequester = remember { FocusRequester() }
  val coroutineScope = rememberCoroutineScope()
  val dismissSheet: () -> Unit = remember {
    {
      coroutineScope.launch {
        sheetState.hide()
        onDismiss()
      }
    }
  }
  var taskTitle by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    snapshotFlow { sheetState.currentValue }.collect {
      if (it == SheetValue.Expanded) {
        focusRequester.requestFocus()
      }
    }
  }

  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = dismissSheet,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .navigationBarsPadding()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = stringResource(Res.string.tasks_add_title),
          style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.weight(1f))
        IconButton(
          onClick = dismissSheet,
          content = {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = "",
              modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp)
                .clip(CircleShape),
            )
          },
        )
      }
    }

    OutlinedTextField(
      value = taskTitle,
      onValueChange = { taskTitle = it },
      modifier = Modifier.fillMaxWidth()
        .focusRequester(focusRequester)
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp),
      placeholder = { Text(stringResource(Res.string.tasks_add_input_hint)) },
      singleLine = true,
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(
        onDone = {
          if (taskTitle.isNotBlank()) {
            onSubmit(taskTitle.trim())
          }
          taskTitle = ""
        }
      ),
    )

    Button(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
      enabled = taskTitle.isNotBlank(),
      onClick = {
        if (taskTitle.isNotBlank()) {
          onSubmit(taskTitle.trim())
        }
        taskTitle = ""
      },
      content = {
        Text(stringResource(Res.string.tasks_add_submit))
      }
    )
  }
}
