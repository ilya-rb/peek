package com.illiarb.peek.uikit.core.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.illiarb.peek.uikit.resources.Res
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch

@Composable
public fun LocalLottieAnimation(
  modifier: Modifier = Modifier,
  animationType: LottieAnimationType,
) {
  val scope = rememberCoroutineScope()
  var imageJson by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    scope.launch {
      imageJson = Res.readBytes(path = "files/${animationType.fileName}.json").decodeToString()
    }
  }

  imageJson?.let { json ->
    val composition by rememberLottieComposition(
      LottieCompositionSpec.JsonString(json)
    )
    val progress by animateLottieCompositionAsState(
      composition = composition,
      iterations = Int.MAX_VALUE,
    )

    Image(
      modifier = modifier,
      contentDescription = "Error animation",
      painter = rememberLottiePainter(
        composition = composition,
        progress = { progress },
      ),
    )
  }
}

public enum class LottieAnimationType(public val fileName: String) {
  ERROR("anim_error"),
  ARTICLES_EMPTY("anim_empty")
}