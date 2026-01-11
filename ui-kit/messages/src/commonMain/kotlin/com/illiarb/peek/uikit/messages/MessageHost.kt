package com.illiarb.peek.uikit.messages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.slack.circuit.overlay.LocalOverlayHost
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
public fun MessageHost(messageProvider: MessageProvider) {
  val overlayHost = LocalOverlayHost.current
  val scope = rememberCoroutineScope()

  LaunchedEffect(messageProvider) {
    messageProvider.messages.collect { message ->
      scope.launch {
        overlayHost.show(MessageOverlay(message))
      }
    }
  }
}

private class MessageOverlay(private val message: Message) : Overlay<Unit> {

  @Composable
  override fun Content(navigator: OverlayNavigator<Unit>) {
    val offsetY = remember { Animatable(MESSAGE_INITIAL_OFFSET) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
      animateToast(alpha, offsetY)

      navigator.finish(Unit)
    }

    Box(
      contentAlignment = Alignment.BottomCenter,
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding(),
    ) {
      ToastMessage(
        message = message.content,
        type = message.type,
        offsetY = offsetY.value,
        alpha = alpha.value,
      )
    }
  }

  private suspend fun CoroutineScope.animateToast(
    alpha: Animatable<Float, AnimationVector1D>,
    offset: Animatable<Float, AnimationVector1D>,
  ) {
    val alphaIn = launch {
      alpha.animateTo(1f, tween(MESSAGE_FADE_DURATION_MS))
    }
    offset.animateTo(
      targetValue = 0f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow,
      ),
    )
    alphaIn.join()

    delay(MESSAGE_DISPLAY_DURATION_MS)

    val alphaOut = launch {
      alpha.animateTo(0f, tween(MESSAGE_FADE_DURATION_MS))
    }
    offset.animateTo(
      targetValue = MESSAGE_INITIAL_OFFSET,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
      ),
    )
    alphaOut.join()
  }

  companion object {
    private const val MESSAGE_DISPLAY_DURATION_MS = 3000L
    private const val MESSAGE_FADE_DURATION_MS = 250
    private const val MESSAGE_INITIAL_OFFSET = 200f
  }
}

@Composable
private fun ToastMessage(
  message: String,
  type: MessageType,
  offsetY: Float,
  alpha: Float,
  modifier: Modifier = Modifier,
) {
  val containerColor = when (type) {
    MessageType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
    MessageType.ERROR -> MaterialTheme.colorScheme.errorContainer
  }

  val contentColor = when (type) {
    MessageType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
    MessageType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
  }

  val icon = when (type) {
    MessageType.SUCCESS -> Icons.Default.Check
    MessageType.ERROR -> Icons.Default.Close
  }

  Box(
    contentAlignment = Alignment.BottomCenter,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 16.dp)
      .graphicsLayer {
        this.translationY = offsetY
        this.alpha = alpha
      },
  ) {
    Surface(
      shape = MaterialTheme.shapes.medium,
      color = containerColor,
      shadowElevation = 4.dp,
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = message,
          style = MaterialTheme.typography.bodyMedium,
          color = contentColor,
        )
      }
    }
  }
}
