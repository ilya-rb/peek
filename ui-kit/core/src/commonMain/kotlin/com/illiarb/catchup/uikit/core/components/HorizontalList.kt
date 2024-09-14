package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.model.Identifiable

@Composable
fun <T> HorizontalList(
  modifier: Modifier = Modifier,
  items: List<T>,
  itemContent: @Composable LazyItemScope.(Int, T) -> Unit,
) where T : Identifiable<String> {
  LazyRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    itemsIndexed(
      items = items,
      key = { _, model -> model.id },
      itemContent = itemContent,
    )
  }
}
