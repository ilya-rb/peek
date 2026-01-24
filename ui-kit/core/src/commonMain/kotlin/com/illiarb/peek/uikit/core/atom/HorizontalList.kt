package com.illiarb.peek.uikit.core.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
public fun <T> HorizontalList(
  modifier: Modifier = Modifier,
  items: ImmutableList<T>,
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

@Preview
@Composable
private fun HorizontalListPreviewLight() {
  PreviewTheme(darkMode = false) {
    HorizontalListPreview()
  }
}

@Preview
@Composable
private fun HorizontalListPreviewDark() {
  PreviewTheme(darkMode = true) {
    HorizontalListPreview()
  }
}

@Composable
private fun HorizontalListPreview() {
  val items = persistentListOf("Item 1", "Item 2", "Item 3")

  HorizontalList(
    modifier = Modifier.padding(16.dp),
    items = items,
    keyProvider = { index, _ -> index }
  ) { _, item ->
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.surfaceContainer,
      modifier = Modifier.padding(horizontal = 4.dp)
    ) {
      Text(
        text = item,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}
