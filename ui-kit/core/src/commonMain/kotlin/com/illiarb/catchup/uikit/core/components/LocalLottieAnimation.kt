package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.illiarb.catchup.uikit.resources.Res
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun LocalLottieAnimation(modifier: Modifier = Modifier, fileName: String) {
  val scope = rememberCoroutineScope()
  var imageJson by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(Unit) {
    scope.launch {
      imageJson = Res.readBytes("files/${fileName}.json").decodeToString()
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
        progress = progress,
      ),
    )
  }
}
