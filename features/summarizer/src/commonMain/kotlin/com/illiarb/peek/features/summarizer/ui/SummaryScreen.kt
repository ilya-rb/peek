package com.illiarb.peek.features.summarizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.summarizer.ui.SummaryScreenContract.Event
import com.illiarb.peek.features.summarizer.ui.SummaryScreenContract.State.ArticleWithSummary
import com.illiarb.peek.uikit.core.atom.StreamingText
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerBox
import com.illiarb.peek.uikit.core.atom.shimmer.ShimmerColumn
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.navigation.NavigationButton
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_open_in_browser
import com.illiarb.peek.uikit.resources.acsb_icon_assistant
import com.illiarb.peek.uikit.resources.summary_loading_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SummaryScreen(
  state: SummaryScreenContract.State,
  screen: SummaryScreen,
  modifier: Modifier = Modifier,
) {
  val eventSink = state.eventSink
  val containerColor = MaterialTheme.colorScheme.surfaceContainerLow

  Scaffold(
    containerColor = containerColor,
    topBar = {
      UiKitTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor),
        title = {},
        windowInsets = WindowInsets(0, 0, 0, 0),
        navigationButton = NavigationButton.Cross,
        onNavigationButtonClick = {
          eventSink.invoke(Event.NavigationIconClick)
        },
        actions = {
          SummaryActions(state, screen.context)
        },
      )
    },
  ) { innerPadding ->
    SummaryContent(
      modifier = modifier.fillMaxSize().padding(innerPadding),
      article = state.articleWithSummary,
      onErrorActionClick = { eventSink(Event.ErrorRetryClicked) },
    )
  }
}

@Composable
private fun SummaryActions(
  state: SummaryScreenContract.State,
  context: SummaryScreen.Context,
  modifier: Modifier = Modifier,
) {
  val eventSink = state.eventSink

  when {
    context == SummaryScreen.Context.READER -> Unit

    state.articleWithSummary is Async.Loading -> {
      CircularProgressIndicator(
        strokeWidth = 2.dp,
        modifier = Modifier.padding(end = 16.dp).size(24.dp)
      )
    }

    state.articleWithSummary is Async.Content -> {
      IconButton(
        onClick = {
          eventSink.invoke(Event.OpenInReaderClick(state.articleWithSummary.content.article))
        },
        content = {
          Icon(
            imageVector = Icons.Filled.OpenInBrowser,
            contentDescription = stringResource(Res.string.acsb_action_open_in_browser),
          )
        },
      )
    }

    else -> Unit
  }
}

@Composable
private fun SummaryContent(
  article: Async<ArticleWithSummary>,
  onErrorActionClick: () -> Unit,
  modifier: Modifier,
) {
  when (article) {
    is Async.Error -> ErrorEmptyState(onButtonClick = onErrorActionClick, modifier = modifier)
    is Async.Loading -> SummaryLoading(modifier)
    is Async.Content -> {
      val summary = article.content.summary

      Column(
        modifier = modifier
          .padding(horizontal = 16.dp)
          .navigationBarsPadding()
      ) {
        Text(
          modifier = Modifier.padding(top = 16.dp),
          text = "${summary.model} · ${summary.price.amount} ${summary.price.currency.code}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
        StreamingText(
          modifier = Modifier.padding(top = 24.dp),
          text = summary.content,
        )
      }
    }
  }
}

@Composable
internal fun SummaryLoading(modifier: Modifier) {
  ShimmerColumn(modifier = modifier.padding(horizontal = 16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = Icons.Filled.Assistant,
        contentDescription = stringResource(Res.string.acsb_icon_assistant),
      )
      Text(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = stringResource(Res.string.summary_loading_title),
        style = MaterialTheme.typography.bodyLarge,
      )
    }
    ShimmerBox(
      modifier = Modifier
        .padding(top = 16.dp)
        .size(width = 250.dp, height = 16.dp),
    )
    ShimmerBox(
      modifier = Modifier
        .padding(top = 8.dp)
        .size(width = 150.dp, height = 16.dp),
    )
  }
}
