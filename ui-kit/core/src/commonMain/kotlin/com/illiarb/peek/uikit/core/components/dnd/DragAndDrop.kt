package com.illiarb.peek.uikit.core.components.dnd

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput

private val DropAnimationSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)

@Composable
public fun rememberDragAndDropState(
  listState: LazyListState,
  draggableItemsCount: Int,
  onMove: (Int, Int) -> Unit,
  onMoveComplete: () -> Unit,
): DragAndDropState {
  val state = remember(listState) {
    DragAndDropState(draggableItemsCount, listState, onMove, onMoveComplete)
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
        animationSpec = DropAnimationSpec,
      )
      state.onDropAnimationComplete()
    }
  }

  return state
}

public fun Modifier.dragAndDropContainer(state: DragAndDropState): Modifier {
  return this.nestedScroll(state.nestedScrollConnection)
}

public fun Modifier.dragHandle(state: DragAndDropState, index: Int): Modifier {
  return this.then(
    pointerInput(state) {
      detectDragGestures(
        onDragStart = {
          if (state.draggingItemIndex == null) {
            state.onDragStartForIndex(index)
          }
        },
        onDrag = { change, offset ->
          change.consume()
          state.onDrag(offset)
        },
        onDragEnd = {
          state.onDragEnd()
        },
        onDragCancel = {
          state.onDragEnd()
        },
      )
    }
  )
}
