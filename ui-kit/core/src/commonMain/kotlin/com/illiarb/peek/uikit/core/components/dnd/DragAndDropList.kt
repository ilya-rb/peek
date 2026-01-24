package com.illiarb.peek.uikit.core.components.dnd

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex

private val SwapAnimationSpec = spring(
  stiffness = Spring.StiffnessMediumLow,
  visibilityThreshold = IntOffset.VisibilityThreshold,
)

public fun <T : Any> LazyListScope.reorderableItems(
  items: List<T>,
  state: DragAndDropState,
  key: (T) -> Any,
  content: @Composable (
    index: Int,
    itemModifier: Modifier,
    dragHandleModifier: Modifier,
    item: T,
  ) -> Unit,
) {
  itemsIndexed(
    items = items,
    key = { _, item -> key(item) },
    contentType = { index, _ -> DraggableItem(index) }
  ) { index, item ->
    val isDragging = state.draggingItemIndex == index
    val isDropping = state.droppingItemIndex == index
    val itemModifier = Modifier.then(
      when {
        isDragging -> Modifier
          .graphicsLayer { translationY = state.currentDelta() }
          .zIndex(1f)

        isDropping -> Modifier.graphicsLayer { translationY = state.currentDelta() }

        else -> Modifier.animateItem(placementSpec = SwapAnimationSpec)
      }
    )

    val dragHandleModifier = Modifier.dragHandle(state, index)

    content(index, itemModifier, dragHandleModifier, item)
  }
}


