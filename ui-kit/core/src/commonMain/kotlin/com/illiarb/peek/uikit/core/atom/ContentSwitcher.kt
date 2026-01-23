package com.illiarb.peek.uikit.core.atom

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
public fun ContentSwitcher(
  first: @Composable (Modifier) -> Unit,
  second: @Composable (Modifier) -> Unit,
  switchEvery: Duration,
  containerHeightDp: Int,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  duration: Duration = 300.milliseconds,
  easing: Easing = FastOutSlowInEasing,
) {
  val durationMillis = duration.inWholeMilliseconds.toInt()
  var showFirst by remember { mutableStateOf(true) }

  LaunchedEffect(enabled) {
    if (!enabled) return@LaunchedEffect

    while (true) {
      delay(switchEvery.inWholeMilliseconds)
      showFirst = !showFirst
    }
  }

  AnimatedContent(
    modifier = modifier,
    targetState = showFirst,
    transitionSpec = {
      val enter = slideInVertically(
        animationSpec = tween(durationMillis, easing = easing),
        initialOffsetY = { containerHeightDp },
      ) + fadeIn()

      val exit = slideOutVertically(
        animationSpec = tween(durationMillis, easing = easing),
        targetOffsetY = { -containerHeightDp }
      ) + fadeOut()

      enter togetherWith exit
    },
  ) { showFirst ->
    val modifier = Modifier.fillMaxWidth()

    if (showFirst) {
      first(modifier)
    } else {
      second(modifier)
    }
  }
}

@Preview
@Composable
private fun ContentSwitcherPreviewLight() {
  PreviewTheme(darkMode = false) {
    ContentSwitcherPreview()
  }
}

@Preview
@Composable
private fun ContentSwitcherPreviewDark() {
  PreviewTheme(darkMode = true) {
    ContentSwitcherPreview()
  }
}

@Composable
private fun ContentSwitcherPreview() {
  ContentSwitcher(
    first = { modifier ->
      Text(
        text = "First Content",
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
      )
    },
    second = { modifier ->
      Text(
        text = "Second Content",
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
      )
    },
    switchEvery = 2.seconds,
    containerHeightDp = 24
  )
}
