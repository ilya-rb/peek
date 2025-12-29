package com.illiarb.peek.uikit.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Duration

@OptIn(ExperimentalAnimationApi::class)
@Composable
public fun TextSwitcher(
  first: @Composable (Modifier) -> Unit,
  second: @Composable (Modifier) -> Unit,
  switchEvery: Duration,
  containerHeightDp: Int,
  modifier: Modifier = Modifier,
  durationMillis: Int = 300,
  easing: Easing = FastOutSlowInEasing,
) {
  var showFirst by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
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
