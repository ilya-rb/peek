package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.popup.ShareAction
import com.illiarb.peek.uikit.core.components.popup.SummarizeAction

@Composable
public fun ArticleCell(
  modifier: Modifier = Modifier,
  title: String,
  saved: Boolean,
  onClick: () -> Unit,
  onBookmarkClick: () -> Unit,
  onSummarizeClick: () -> Unit,
  onShareClick: () -> Unit,
  subtitle: String? = null,
  caption: String? = null,
  badge: String? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.clickable(onClick = onClick),
  ) {
    ArticleContent(title = title, subtitle = subtitle, caption = caption, badge = badge)
    ArticleActions(saved, onBookmarkClick, onSummarizeClick, onShareClick)
  }
}

@Composable
private fun RowScope.ArticleContent(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String?,
  caption: String?,
  badge: String?,
) {
  Column(modifier = modifier.weight(1f)) {
    val hasSubtitle = subtitle != null
    val paddingVertical = 12.dp

    if (hasSubtitle) {
      Text(
        text = subtitle.uppercase(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .fillMaxWidth()
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

    if (caption != null || badge != null) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(
          start = 16.dp,
          end = 16.dp,
          top = 8.dp,
          bottom = paddingVertical,
        ),
      ) {
        if (caption != null) {
          Text(
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        if (badge != null) {
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.errorContainer,
          ) {
            Text(
              text = badge,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onErrorContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ArticleActions(
  articleSaved: Boolean,
  onBookmarkClick: () -> Unit,
  onSummarizeClick: () -> Unit,
  onShareClick: () -> Unit,
) {
  var moreMenuExpanded by remember { mutableStateOf(false) }

  Column(
    verticalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxHeight()
  ) {
    BookmarkButton(
      saved = articleSaved,
      onClick = onBookmarkClick,
    )

    ArticlePopupMenu(
      expanded = moreMenuExpanded,
      onMenuClick = { moreMenuExpanded = true },
      onDismiss = { moreMenuExpanded = false },
      onSummarizeClick = {
        moreMenuExpanded = false
        onSummarizeClick()
      },
      onShareClick = {
        moreMenuExpanded = false
        onShareClick()
      }
    )
  }
}

@Composable
private fun BookmarkButton(
  saved: Boolean,
  onClick: () -> Unit,
) {
  var bouncing by remember { mutableStateOf(false) }
  val scale by animateFloatAsState(
    targetValue = if (bouncing) 1.2f else 1f,
    animationSpec = tween(durationMillis = if (bouncing) 100 else 200),
    finishedListener = { if (bouncing) bouncing = false },
  )

  IconButton(
    onClick = {
      bouncing = true
      onClick()
    },
    modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
  ) {
    Icon(
      imageVector = if (saved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
      tint = MaterialTheme.colorScheme.primary,
      contentDescription = null,
    )
  }
}

@Composable
private fun ArticlePopupMenu(
  onMenuClick: () -> Unit,
  onDismiss: () -> Unit,
  expanded: Boolean,
  onSummarizeClick: () -> Unit,
  onShareClick: () -> Unit,
) {
  Box {
    val iconColors = if (expanded) {
      IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.onPrimaryFixedVariant
      )
    } else {
      null
    }

    if (iconColors != null) {
      IconButton(onClick = onMenuClick, colors = iconColors) {
        MoreIcon()
      }
    } else {
      IconButton(onClick = onMenuClick) {
        MoreIcon()
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = onDismiss,
      offset = DpOffset((-8).dp, 0.dp),
    ) {
      SummarizeAction(onClick = onSummarizeClick)
      ShareAction(onClick = onShareClick)
    }
  }
}

@Composable
private fun MoreIcon() {
  Icon(
    imageVector = Icons.Filled.MoreHoriz,
    tint = MaterialTheme.colorScheme.primary,
    contentDescription = null,
  )
}
