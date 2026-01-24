package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
public fun ListHeader(
  title: String,
  style: TextStyle = MaterialTheme.typography.titleLarge,
  modifier: Modifier = Modifier,
  icon: VectorIcon? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .fillMaxWidth(),
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon.imageVector,
        contentDescription = icon.contentDescription,
        modifier = Modifier.size(20.dp),
      )
    }

    Text(
      text = title,
      modifier = Modifier.padding(start = if (icon != null) 8.dp else 0.dp),
      style = style,
    )
  }
}

@Preview
@Composable
private fun ListHeaderFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    ListHeaderFullPreview()
  }
}

@Preview
@Composable
private fun ListHeaderFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    ListHeaderFullPreview()
  }
}

@Composable
private fun ListHeaderFullPreview() {
  ListHeader(
    title = "Section Title",
    icon = VectorIcon(Icons.Filled.Settings, contentDescription = "")
  )
}

@Preview
@Composable
private fun ListHeaderStatesPreview() {
  PreviewTheme(darkMode = false) {
    ListHeaderStatesPreviewContent()
  }
}

@Composable
private fun ListHeaderStatesPreviewContent() {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    ListHeader(title = "Title only")

    ListHeader(
      title = "With icon",
      icon = VectorIcon(Icons.Filled.Settings, contentDescription = "")
    )
  }
}
