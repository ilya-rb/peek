package com.illiarb.peek.features.tasks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun AddTaskBottomSheet(
  onDismiss: () -> Unit,
  onSubmit: (String) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var taskTitle by remember { mutableStateOf("") }
  val focusRequester = remember { FocusRequester() }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  ModalBottomSheet(
    onDismissRequest = {
      coroutineScope.launch {
        sheetState.hide()
        onDismiss()
      }
    },
    sheetState = sheetState,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .navigationBarsPadding()
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "I need to",
          style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.weight(1f))
        IconButton(
          onClick = {
            coroutineScope.launch {
              sheetState.hide()
              onDismiss()
            }
          },
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

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
      value = taskTitle,
      onValueChange = { taskTitle = it },
      modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
      placeholder = {
        Text("What do you need to do?")
      },
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

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = {
        if (taskTitle.isNotBlank()) {
          onSubmit(taskTitle.trim())
        }
        taskTitle = ""
      },
      modifier = Modifier.fillMaxWidth(),
      enabled = taskTitle.isNotBlank(),
    ) {
      Text("Save")
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}
