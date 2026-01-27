package com.illiarb.peek.uikit.core.atom

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
public fun StreamingText(
  text: String,
  modifier: Modifier = Modifier,
) {
  var visibleCharCount by remember { mutableIntStateOf(0) }

  LaunchedEffect(text) {
    visibleCharCount = 0

    for (i in text.indices) {
      delay(15.milliseconds)
      visibleCharCount = i + 1
    }
  }

  Text(
    text = text.take(visibleCharCount),
    modifier = modifier,
    style = MaterialTheme.typography.bodyLarge,
  )
}
