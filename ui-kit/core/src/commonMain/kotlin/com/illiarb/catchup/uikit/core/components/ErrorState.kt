package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.home_articles_empty_action
import com.illiarb.catchup.uikit.resources.home_articles_empty_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullscreenErrorState(
  modifier: Modifier = Modifier,
  errorType: ErrorStateKind,
  onRefreshClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    FullscreenState(
      title = stringResource(Res.string.home_articles_empty_title),
      buttonText = stringResource(Res.string.home_articles_empty_action),
      onButtonClick = onRefreshClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(shape = RoundedCornerShape(size = 24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
      LocalLottieAnimation(
        modifier = Modifier.size(200.dp),
        animationType = when (errorType) {
          ErrorStateKind.UNKNOWN -> LottieAnimationType.ERROR
        },
      )
    }
  }
}

enum class ErrorStateKind {
  UNKNOWN
}