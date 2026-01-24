package com.illiarb.peek.uikit.core.components.dnd

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.channels.Channel

public class DragAndDropState(
  public val draggableItemsCount: Int,
  public val listState: LazyListState,
  public val onMove: (Int, Int) -> Unit,
  public val onMoveComplete: () -> Unit,
) {

  internal val animatedDelta = Animatable(0f)

  internal var draggingItemIndex: Int? by mutableStateOf(null)
  internal var droppingItemIndex: Int? by mutableStateOf(null)
  internal var scrollChannel = Channel<Float>()
  internal var dropChannel = Channel<Float>()
  internal var draggingItem: LazyListItemInfo? = null

  private var delta: Float by mutableFloatStateOf(0f)

  internal val nestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
      // Consume all vertical scroll when dragging
      return if (draggingItemIndex != null || droppingItemIndex != null) {
        Offset(0f, available.y)
      } else {
        Offset.Zero
      }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
      // Consume fling when dragging
      return if (draggingItemIndex != null || droppingItemIndex != null) {
        available
      } else {
        Velocity.Zero
      }
    }
  }

  internal fun onDragStartForIndex(index: Int) {
    listState.layoutInfo.visibleItemsInfo
      .firstOrNull { item -> (item.contentType as? DraggableItem)?.index == index }
      ?.let { item ->
        draggingItem = item
        draggingItemIndex = index
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
