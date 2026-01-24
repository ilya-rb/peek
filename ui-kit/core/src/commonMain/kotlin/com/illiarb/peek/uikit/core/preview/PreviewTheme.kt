package com.illiarb.peek.uikit.core.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.theme.UiKitTheme

@Composable
public fun PreviewTheme(
  darkMode: Boolean,
  fullscreen: Boolean = false,
  padding: Boolean = false,
  content: @Composable ColumnScope.() -> Unit
) {
  UiKitTheme(useDarkTheme = darkMode, useDynamicColors = false) {
    Column(
      content = content,
      modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .let {
          if (fullscreen) {
            it.fillMaxSize()
          } else {
            it.fillMaxWidth()
          }
        }
        .padding(if (padding) 20.dp else 0.dp)
    )
  }
}
