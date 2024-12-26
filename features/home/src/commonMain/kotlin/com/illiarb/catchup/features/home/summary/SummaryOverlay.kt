package com.illiarb.catchup.features.home.summary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator

internal interface SummaryOverlayContract {

  data class Model(
    val summary: Async<ArticleSummary>,
  )
}

internal expect suspend fun OverlayHost.showSummaryOverlay(model: SummaryOverlayContract.Model)

@Composable
internal fun SummaryOverlay(
  model: SummaryOverlayContract.Model,
  navigator: OverlayNavigator<Unit>,
) {
  val scrollState = rememberScrollState()

  Box(
    Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .navigationBarsPadding()
  ) {
    when (val summary = model.summary) {
      is Async.Loading, is Async.Error -> TODO()
      is Async.Content -> {
        Text(
          text = summary.content.content,
          modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}