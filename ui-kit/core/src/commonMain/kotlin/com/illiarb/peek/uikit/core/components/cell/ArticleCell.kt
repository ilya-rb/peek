package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.popup.ShareAction
import com.illiarb.peek.uikit.core.components.popup.SummarizeAction
import kotlinx.coroutines.launch

@Composable
public fun ArticleCell(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String,
  saved: Boolean,
  onClick: () -> Unit,
  onBookmarkClick: () -> Unit,
  onSummarizeClick: () -> Unit,
  onShareClick: () -> Unit,
  caption: String? = null,
) {
  var moreMenuExpanded by remember { mutableStateOf(false) }

  val bookmarkBounce = remember { Animatable(1f) }
  val coroutineScope = rememberCoroutineScope()

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.clickable(onClick = onClick),
  ) {
    Column(modifier = Modifier.weight(1f)) {
      val hasSubtitle = subtitle.isNotEmpty()
      val paddingVertical = 12.dp

      if (hasSubtitle) {
        Text(
          text = subtitle.uppercase(),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.fillMaxWidth()
            .padding(top = paddingVertical, start = 16.dp, end = 24.dp),
        )
      }

      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        overflow = TextOverflow.Ellipsis,
        maxLines = 4,
        modifier = Modifier.fillMaxWidth().padding(
          start = 16.dp,
          end = 16.dp,
          top = if (hasSubtitle) 8.dp else paddingVertical,
          bottom = if (caption == null) paddingVertical else 0.dp,
        ),
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (caption != null) {
          Text(
            modifier = Modifier.padding(
              start = 16.dp,
              end = 16.dp,
              top = 8.dp,
              bottom = paddingVertical,
            ),
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }

    Column(
      verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxHeight()
    ) {
      IconButton(
        onClick = {
          coroutineScope.launch {
            bookmarkBounce.animateTo(1.2f, animationSpec = tween(100))
            bookmarkBounce.animateTo(1f, animationSpec = tween(200))
          }
          onBookmarkClick()
        }, modifier = Modifier.graphicsLayer(
          scaleX = bookmarkBounce.value, scaleY = bookmarkBounce.value
        )
      ) {
        Icon(
          imageVector = if (saved) {
            Icons.Filled.Bookmark
          } else {
            Icons.Filled.BookmarkBorder
          },
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = null,
        )
      }

      Box {
        IconButton(onClick = { moreMenuExpanded = true }) {
          Icon(
            imageVector = Icons.Filled.MoreHoriz,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
        DropdownMenu(
          expanded = moreMenuExpanded,
          onDismissRequest = { moreMenuExpanded = false },
        ) {
          SummarizeAction {
            moreMenuExpanded = false
            onSummarizeClick()
          }
          ShareAction {
            moreMenuExpanded = false
            onShareClick()
          }
        }
      }
    }
  }
}
