package com.illiarb.peek.uikit.core.components.dnd

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.channels.Channel

@Composable
public fun rememberReorderableState(
  listState: LazyListState,
  draggableItemsCount: Int,
  onMove: (Int, Int) -> Unit,
  onMoveComplete: () -> Unit,
): ReorderableState {
  val state = remember(listState) {
    ReorderableState(draggableItemsCount, listState, onMove, onMoveComplete)
  }
  LaunchedEffect(Unit) {
    while (true) {
      val diff = state.scrollChannel.receive()
      listState.scrollBy(diff)
    }
  }
  LaunchedEffect(Unit) {
    while (true) {
      val dropDelta = state.dropChannel.receive()
      state.animatedDelta.snapTo(dropDelta)
      state.animatedDelta.animateTo(
        targetValue = 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      )
      state.onDropAnimationComplete()
    }
  }
  return state
}

public fun Modifier.reorderableContainer(state: ReorderableState): Modifier {
  return this.then(pointerInput(key1 = state) {
    detectDragGesturesAfterLongPress(
      onDrag = { change, offset ->
        change.consume()
        state.onDrag(offset)
      },
      onDragStart = { offset -> state.onDragStart(offset) },
      onDragEnd = { state.onDragEnd() },
      onDragCancel = { state.onDragEnd() },
    )
  })
}

public fun <T : Any> LazyListScope.reorderableItems(
  items: List<T>,
  state: ReorderableState,
  key: (T) -> Any,
  content: @Composable (Modifier, T) -> Unit,
) {
  itemsIndexed(
    items = items,
    key = { _, item -> key(item) },
    contentType = { index, _ -> DraggableItem(index) }
  ) { index, item ->
    val isDragging = state.draggingItemIndex == index
    val isDropping = state.droppingItemIndex == index
    val modifier = Modifier
      .clickable(onClick = {})
      .then(
        when {
          isDragging -> Modifier
            .graphicsLayer { translationY = state.currentDelta() }
            .zIndex(1f)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

          isDropping -> Modifier
            .graphicsLayer { translationY = state.currentDelta() }
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

          else -> Modifier.animateItem(
            placementSpec = spring(
              stiffness = Spring.StiffnessMediumLow,
              visibilityThreshold = IntOffset.VisibilityThreshold,
            )
          )
        }
      )

    content(modifier, item)
  }
}

public data class ReorderableState(
  val draggableItemsCount: Int,
  val listState: LazyListState,
  val onMove: (Int, Int) -> Unit,
  val onMoveComplete: () -> Unit,
) {

  internal var draggingItemIndex: Int? by mutableStateOf(null)
  internal var droppingItemIndex: Int? by mutableStateOf(null)
  private var delta: Float by mutableFloatStateOf(0f)
  internal val animatedDelta = Animatable(0f)
  internal var scrollChannel = Channel<Float>()
  internal var dropChannel = Channel<Float>()
  internal var draggingItem: LazyListItemInfo? = null

  internal fun onDragStart(offset: Offset) {
    listState.layoutInfo.visibleItemsInfo
      .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
      ?.let { item ->
        (item.contentType as? DraggableItem)?.let { draggableItem ->
          draggingItem = item
          draggingItemIndex = draggableItem.index
        }
      }
  }

  internal fun onDragEnd() {
    droppingItemIndex = draggingItemIndex
    draggingItem = null
    draggingItemIndex = null
    dropChannel.trySend(delta)
    delta = 0f
  }

  internal fun onDropAnimationComplete() {
    droppingItemIndex = null
    onMoveComplete()
  }

  internal fun currentDelta(): Float {
    return when {
      draggingItemIndex != null -> delta
      droppingItemIndex != null -> animatedDelta.value
      else -> 0f
    }
  }

  internal fun onDrag(offset: Offset) {
    delta += offset.y

    val currentDraggingItemIndex = draggingItemIndex ?: return
    val currentDraggingItem = draggingItem ?: return

    val startOffset = currentDraggingItem.offset + delta
    val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
    val midOffset = startOffset + (endOffset - startOffset) / 2

    val targetItem = listState.layoutInfo.visibleItemsInfo.find { item ->
      midOffset.toInt() in item.offset..item.offset + item.size
        && currentDraggingItem.index != item.index
        && item.contentType is DraggableItem
    }

    if (targetItem != null) {
      val targetIndex = (targetItem.contentType as DraggableItem).index
      onMove(currentDraggingItemIndex, targetIndex)
      draggingItemIndex = targetIndex
      delta += currentDraggingItem.offset - targetItem.offset
      draggingItem = targetItem
    } else {
      val startOffsetToTop = startOffset - listState.layoutInfo.viewportStartOffset
      val endOffsetToTop = endOffset - listState.layoutInfo.viewportEndOffset
      val scroll = when {
        startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
        endOffsetToTop > 0 -> endOffsetToTop.coerceAtLeast(0f)
        else -> 0f
      }
      if (scroll != 0f
        && currentDraggingItemIndex != 0
        && currentDraggingItemIndex != draggableItemsCount - 1
      ) {
        scrollChannel.trySend(scroll)
      }
    }
  }
}

internal data class DraggableItem(val index: Int)
