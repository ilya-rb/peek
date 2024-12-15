package com.illiarb.catchup.uikit.core.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.author_text
import org.jetbrains.compose.resources.stringResource

@Composable
public fun AuthorText(
  modifier: Modifier = Modifier,
  author: String,
  style: TextStyle,
  color: Color,
) {
  Text(
    modifier = modifier,
    text = stringResource(Res.string.author_text, author),
    style = style,
    color = color,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
  )
}