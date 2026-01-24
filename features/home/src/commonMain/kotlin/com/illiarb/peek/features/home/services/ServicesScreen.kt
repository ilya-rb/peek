package com.illiarb.peek.features.home.services

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.uikit.core.atom.BoxListItemContainer
import com.illiarb.peek.uikit.core.components.cell.ListHeader
import com.illiarb.peek.uikit.core.components.cell.RowCell
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.EndAction
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.StartContent
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.TextModel
import com.illiarb.peek.uikit.core.components.dnd.dragAndDropContainer
import com.illiarb.peek.uikit.core.components.dnd.rememberDragAndDropState
import com.illiarb.peek.uikit.core.components.dnd.reorderableItems
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_drag_handle
import com.illiarb.peek.uikit.resources.dou_logo
import com.illiarb.peek.uikit.resources.ft_logo
import com.illiarb.peek.uikit.resources.hn_logo
import com.illiarb.peek.uikit.resources.service_dou_name
import com.illiarb.peek.uikit.resources.service_ft_name
import com.illiarb.peek.uikit.resources.service_hacker_news_name
import com.illiarb.peek.uikit.resources.services_diaog_title
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
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
    mutableStateOf(state.sources.content.toPersistentList())
  }
  val listState = rememberLazyListState()
  val reorderableState = rememberDragAndDropState(
    listState = listState,
    draggableItemsCount = items.size,
    onMove = { from, to ->
      items = items.mutate {
        it.add(to, it.removeAt(from))
      }
    },
    onMoveComplete = {
      eventSink.invoke(ServicesScreenContract.Event.ItemsReordered(items))
    }
  )

  Column(modifier = modifier) {
    ListHeader(title = stringResource(Res.string.services_diaog_title))

    LazyColumn(
      state = listState,
      modifier = Modifier
        .dragAndDropContainer(reorderableState)
        .padding(vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      reorderableItems(
        items = items,
        state = reorderableState,
        key = { source -> source.kind },
        content = { index, itemModifier, dragHandleModifier, source ->
          BoxListItemContainer(
            index = index,
            itemsCount = items.size,
            modifier = itemModifier.fillMaxWidth().padding(horizontal = 16.dp),
          ) {
            ServiceItem(source, dragHandleModifier)
          }
        },
      )
    }
  }
}

@Composable
private fun ServiceItem(
  source: NewsSource,
  dragHandleModifier: Modifier,
  modifier: Modifier = Modifier,
) {
  RowCell(
    modifier = modifier,
    title = TextModel(
      when (source.kind) {
        NewsSourceKind.Dou -> stringResource(Res.string.service_dou_name)
        NewsSourceKind.HackerNews -> stringResource(Res.string.service_hacker_news_name)
        NewsSourceKind.Ft -> stringResource(Res.string.service_ft_name)
      }
    ),
    startContent = StartContent.Avatar(
      when (source.kind) {
        NewsSourceKind.HackerNews -> Res.drawable.hn_logo
        NewsSourceKind.Dou -> Res.drawable.dou_logo
        NewsSourceKind.Ft -> Res.drawable.ft_logo
      }
    ),
    endAction = EndAction.Icon(
      modifier = dragHandleModifier,
      icon = VectorIcon(
        imageVector = Icons.Filled.DragHandle,
        contentDescription = stringResource(Res.string.acsb_icon_drag_handle),
        tint = MaterialTheme.colorScheme.primary
      )
    )
  )
}
