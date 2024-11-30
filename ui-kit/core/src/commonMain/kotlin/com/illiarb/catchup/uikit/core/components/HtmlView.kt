package com.illiarb.catchup.uikit.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
expect fun HtmlView(
  modifier: Modifier = Modifier,
  content: String,
  style: TextStyle,
)