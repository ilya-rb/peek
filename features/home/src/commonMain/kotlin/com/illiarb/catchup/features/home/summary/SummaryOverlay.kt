package com.illiarb.catchup.features.home.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_close
import com.illiarb.catchup.uikit.resources.acsb_action_open_in_browser
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator
import org.jetbrains.compose.resources.stringResource

internal interface SummaryOverlayContract {

  data class Model(
    val article: Article,
    val summary: ArticleSummary,
  )

  sealed interface Result {
    data object Close : Result
    data class OpenInReader(val article: Article) : Result
  }
}

internal expect suspend fun OverlayHost.showSummaryOverlay(
  model: SummaryOverlayContract.Model,
  containerColor: Color,
): SummaryOverlayContract.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SummaryOverlay(
  containerColor: Color,
  model: SummaryOverlayContract.Model,
  navigator: OverlayNavigator<SummaryOverlayContract.Result>,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = containerColor),
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
        },
        navigationIcon = {
          IconButton(onClick = { navigator.finish(SummaryOverlayContract.Result.Close) }) {
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = stringResource(Res.string.acsb_action_close),
            )
          }
        },
        actions = {
          IconButton(
            onClick = {
              navigator.finish(SummaryOverlayContract.Result.OpenInReader(model.article))
            },
            content = {
              Icon(
                imageVector = Icons.Filled.OpenInBrowser,
                contentDescription = stringResource(Res.string.acsb_action_open_in_browser),
              )
            },
          )
        },
      )
    },
  ) { innerPadding ->
    Box(
      Modifier
        .background(containerColor)
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Text(
        text = model.summary.content,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}