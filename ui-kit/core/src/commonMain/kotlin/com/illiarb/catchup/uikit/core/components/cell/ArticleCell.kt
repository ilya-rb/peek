package com.illiarb.catchup.uikit.core.components.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.text.AuthorText
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_summarize
import com.illiarb.catchup.uikit.resources.reader_action_summarize
import org.jetbrains.compose.resources.stringResource

@Composable
public fun ArticleCell(
  modifier: Modifier = Modifier,
  title: String,
  caption: String,
  saved: Boolean,
  onClick: () -> Unit,
  onBookmarkClick: () -> Unit,
  onSummarizeClick: () -> Unit,
  author: String? = null,
) {
  var moreMenuExpanded by remember { mutableStateOf(false) }

  Column(modifier = modifier.clickable { onClick.invoke() }) {
    val hasCaption = caption.isNotEmpty()
    val paddingTop = 12.dp

    if (hasCaption) {
      Text(
        text = caption.uppercase(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(top = paddingTop, start = 16.dp, end = 24.dp),
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
        top = if (hasCaption) 0.dp else paddingTop,
      ),
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      if (author != null) {
        // TODO: Support large text correct trim
        AuthorText(
          modifier = Modifier.padding(horizontal = 16.dp),
          author = author,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Spacer(Modifier.weight(1f))

      IconButton(onClick = onBookmarkClick) {
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
          DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.reader_action_summarize)) },
            onClick = onSummarizeClick,
            trailingIcon = {
              Icon(
                imageVector = Icons.Filled.Summarize,
                contentDescription = stringResource(Res.string.acsb_action_summarize),
              )
            }
          )
        }
      }
    }
  }
}
