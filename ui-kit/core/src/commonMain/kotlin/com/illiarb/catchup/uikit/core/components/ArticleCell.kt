package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ArticleCell(
  modifier: Modifier = Modifier,
  title: String,
  caption: String,
  onClick: () -> Unit = {},
) {
  Column(modifier = modifier.clickable { onClick.invoke() }) {
    if (caption.isNotEmpty()) {
      Text(
        text = caption.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 16.dp, end = 16.dp),
      )
    }

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp),
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
      overflow = TextOverflow.Ellipsis,
      maxLines = 4,
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 8.dp),
    ) {
      Spacer(Modifier.weight(1f))

      Icon(
        imageVector = Icons.Filled.BookmarkBorder,
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = null,
        modifier = Modifier.padding(end = 16.dp).padding(top = 8.dp)
      )

      Icon(
        imageVector = Icons.Filled.MoreHoriz,
        tint = MaterialTheme.colorScheme.onSurface,
        contentDescription = null,
        modifier = Modifier.padding(end = 16.dp).padding(top = 8.dp)
      )
    }
  }
}
