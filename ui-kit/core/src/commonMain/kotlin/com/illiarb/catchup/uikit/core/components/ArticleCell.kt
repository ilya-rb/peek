package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.text.AuthorText

@Composable
fun ArticleCell(
  modifier: Modifier = Modifier,
  title: String,
  author: String? = null,
  caption: String,
  onClick: () -> Unit = {},
) {
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

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 12.dp),
    ) {
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

      Icon(
        imageVector = Icons.Filled.BookmarkBorder,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
        modifier = Modifier.padding(end = 16.dp).padding(top = 8.dp)
      )

      Icon(
        imageVector = Icons.Filled.MoreHoriz,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
        modifier = Modifier.padding(end = 24.dp).padding(top = 8.dp)
      )
    }
  }
}
