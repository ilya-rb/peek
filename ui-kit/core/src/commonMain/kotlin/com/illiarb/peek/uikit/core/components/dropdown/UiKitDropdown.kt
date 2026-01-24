package com.illiarb.peek.uikit.core.components.dropdown

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.theme.UiKitShapes

@Composable
public fun UiKitDropdown(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  offset: DpOffset = DpOffset(0.dp, 0.dp),
  content: @Composable () -> Unit,
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    offset = offset,
    shape = UiKitShapes.medium,
  ) {
    content()
  }
}
