package com.illiarb.catchup.uikit.core.preview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.core.theme.UiKitTheme

@Composable
public fun PreviewTheme(
  darkMode: Boolean,
  fullscreen: Boolean = false,
  innerPadding: Boolean = true,
  content: @Composable () -> Unit
) {
  UiKitTheme(useDarkTheme = darkMode, useDynamicColors = false) {
    Column(
      modifier = Modifier
        .let {
          if (fullscreen) {
            it.fillMaxSize()
          } else {
            it
          }
        }
        .background(MaterialTheme.colorScheme.background)
        .padding(if (innerPadding) 40.dp else 0.dp)
    ) {
      content()
    }
  }
}