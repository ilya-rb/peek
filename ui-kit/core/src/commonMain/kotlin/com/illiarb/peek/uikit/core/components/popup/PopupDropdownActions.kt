package com.illiarb.peek.uikit.core.components.popup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_open_in_browser
import com.illiarb.peek.uikit.resources.acsb_action_share
import com.illiarb.peek.uikit.resources.acsb_action_summarize
import com.illiarb.peek.uikit.resources.reader_action_open_in_browser
import com.illiarb.peek.uikit.resources.reader_action_share
import com.illiarb.peek.uikit.resources.reader_action_summarize
import org.jetbrains.compose.resources.stringResource

@Composable
public fun SummarizeAction(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  DropdownMenuItem(
    modifier = modifier,
    text = { Text(text = stringResource(Res.string.reader_action_summarize)) },
    onClick = onClick,
    leadingIcon = {
      Icon(
        imageVector = Icons.Filled.Checklist,
        contentDescription = stringResource(Res.string.acsb_action_summarize),
      )
    }
  )
}

@Composable
public fun OpenInBrowserAction(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  DropdownMenuItem(
    modifier = modifier,
    text = { Text(text = stringResource(Res.string.reader_action_open_in_browser)) },
    onClick = onClick,
    leadingIcon = {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
        contentDescription = stringResource(Res.string.acsb_action_open_in_browser),
      )
    },
  )
}

@Composable
public fun ShareAction(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  DropdownMenuItem(
    modifier = modifier,
    text = { Text(text = stringResource(Res.string.reader_action_share)) },
    onClick = onClick,
    leadingIcon = {
      Icon(
        imageVector = Icons.Filled.Share,
        contentDescription = stringResource(Res.string.acsb_action_share),
      )
    }
  )
}
