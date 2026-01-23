package com.illiarb.peek.uikit.core.components.cell.internal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun RowCellInternal(
  modifier: Modifier = Modifier,
  title: @Composable () -> Unit,
  subtitle: @Composable () -> Unit,
  startIcon: @Composable () -> Unit,
  endContent: @Composable () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
  ) {
    startIcon.invoke()

    Column {
      title.invoke()
      subtitle.invoke()
    }

    Spacer(Modifier.weight(1f))

    endContent.invoke()
  }
}
