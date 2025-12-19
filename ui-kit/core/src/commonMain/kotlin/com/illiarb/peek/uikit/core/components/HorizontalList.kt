package com.illiarb.peek.uikit.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
public fun <T> HorizontalList(
  modifier: Modifier = Modifier,
  items: List<T>,
  keyProvider: (Int, T) -> Any,
  itemContent: @Composable LazyItemScope.(Int, T) -> Unit,
) {
  LazyRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    itemsIndexed(
      items = items,
      key = keyProvider,
      itemContent = itemContent,
    )
  }
}
