package com.illiarb.peek.features.home.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.uikit.core.components.cell.SelectableCircleAvatar
import com.illiarb.peek.uikit.core.components.dnd.rememberReorderableState
import com.illiarb.peek.uikit.core.components.dnd.reorderableContainer
import com.illiarb.peek.uikit.core.components.dnd.reorderableItems
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.dou_logo
import com.illiarb.peek.uikit.resources.ft_logo
import com.illiarb.peek.uikit.resources.hn_logo
import com.illiarb.peek.uikit.resources.service_dou_name
import com.illiarb.peek.uikit.resources.service_ft_name
import com.illiarb.peek.uikit.resources.service_hacker_news_name
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ServicesScreen(
  state: ServicesScreenContract.State,
  modifier: Modifier,
) {
  if (state.sources !is Async.Content) {
    return
  }

  val eventSink = state.eventSink
  var items by remember {
    mutableStateOf(state.sources.content.toImmutableList())
  }
  val itemsCount by remember {
    derivedStateOf { items.size }
  }
  val listState = rememberLazyListState()
  val reorderableState = rememberReorderableState(
    listState = listState,
    draggableItemsCount = itemsCount,
    onMove = { from, to ->
      items = items.toMutableList()
        .apply { add(to, removeAt(from)) }
        .toImmutableList()
    },
    onMoveComplete = {
      eventSink.invoke(ServicesScreenContract.Event.ItemsReordered(items))
    }
  )

  LazyColumn(
    modifier = Modifier.reorderableContainer(reorderableState).padding(bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    state = listState,
  ) {
    reorderableItems(
      items = items,
      state = reorderableState,
      key = { source -> source.kind },
      content = { modifier, source ->
        ServiceItem(source, modifier)
      },
    )
  }
}

@Composable
private fun ServiceItem(source: NewsSource, modifier: Modifier) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp)
  ) {
    SelectableCircleAvatar(
      image = when (source.kind) {
        NewsSourceKind.HackerNews -> Res.drawable.hn_logo
        NewsSourceKind.Dou -> Res.drawable.dou_logo
        NewsSourceKind.Ft -> Res.drawable.ft_logo
      },
      onClick = {
      }
    )

    Text(
      modifier = Modifier.padding(start = 16.dp),
      text = when (source.kind) {
        NewsSourceKind.Dou -> stringResource(Res.string.service_dou_name)
        NewsSourceKind.HackerNews -> stringResource(Res.string.service_hacker_news_name)
        NewsSourceKind.Ft -> stringResource(Res.string.service_ft_name)
      }
    )

    Spacer(Modifier.weight(1f))

    IconButton(onClick = { }) {
      Icon(
        imageVector = Icons.Filled.DragHandle,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
      )
    }
  }
}
