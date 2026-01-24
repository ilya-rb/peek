package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.illiarb.peek.uikit.core.model.IconStyle
import com.illiarb.peek.uikit.core.model.asContainerModifier
import com.illiarb.peek.uikit.core.model.contentPadding
import com.illiarb.peek.uikit.core.preview.PreviewTheme

public sealed interface ListHeaderStyle {
  public data object Regular : ListHeaderStyle
  public data object Medium : ListHeaderStyle
  public data object Small : ListHeaderStyle
}

@Composable
public fun ListHeader(
  title: String,
  style: ListHeaderStyle = ListHeaderStyle.Regular,
  modifier: Modifier = Modifier,
  startIcon: VectorIcon? = null,
  endIcon: VectorIcon? = null,
  onEndIconClick: (() -> Unit)? = null,
) {
  ListHeader(
    title = { style, modifier -> Text(title, modifier, style = style) },
    style = style,
    modifier = modifier,
    startIcon = startIcon,
    endIcon = endIcon,
    onEndIconClick = onEndIconClick,
  )
}

@Composable
public fun ListHeader(
  title: @Composable (TextStyle, Modifier) -> Unit,
  style: ListHeaderStyle = ListHeaderStyle.Regular,
  modifier: Modifier = Modifier,
  startIcon: VectorIcon? = null,
  endIcon: VectorIcon? = null,
  onEndIconClick: (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .fillMaxWidth(),
  ) {
    if (startIcon != null) {
      Icon(
        imageVector = startIcon.imageVector,
        contentDescription = startIcon.contentDescription,
        modifier = Modifier.size(20.dp),
      )
    }
    val textStyle = when (style) {
      ListHeaderStyle.Regular -> MaterialTheme.typography.titleLarge
      ListHeaderStyle.Medium -> MaterialTheme.typography.titleMedium
      ListHeaderStyle.Small -> MaterialTheme.typography.titleSmall
    }
    val textModifier = Modifier.padding(
      start = if (startIcon != null) {
        8.dp
      } else {
        0.dp
      }
    )

    title(textStyle, textModifier)

    if (endIcon != null) {
      Spacer(Modifier.weight(1f))

      Icon(
        imageVector = endIcon.imageVector,
        contentDescription = endIcon.contentDescription,
        modifier = endIcon.style.asContainerModifier()
          .then(
            if (onEndIconClick != null) {
              Modifier.clickable(onClick = onEndIconClick)
            } else {
              Modifier
            }
          )
          .padding(endIcon.style.contentPadding())
          .size(20.dp),
      )
    }
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
    title = { style, modifier ->
      Text("Section Title", style = style, modifier = modifier)
    },
    startIcon = VectorIcon(Icons.Filled.Settings, contentDescription = "")
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
    ListHeader(
      title = { style, modifier ->
        Text("Section Title", style = style, modifier = modifier)
      }
    )
    ListHeader(
      title = { style, modifier ->
        Text("With icon", style = style, modifier = modifier)
      },
      startIcon = VectorIcon(Icons.Filled.Settings, contentDescription = ""),
      endIcon = VectorIcon(
        imageVector = Icons.Filled.Settings,
        contentDescription = "",
        style = IconStyle.CircleBackground,
      ),
    )
  }
}
