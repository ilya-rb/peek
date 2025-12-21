package com.illiarb.peek.features.reader

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.showScreenOverlay
import com.illiarb.peek.features.reader.ReaderScreenContract.Event
import com.illiarb.peek.uikit.core.components.TopAppBarTitleLoading
import com.illiarb.peek.uikit.core.components.WebView
import com.illiarb.peek.uikit.core.components.cell.ArticleReaderLoading
import com.illiarb.peek.uikit.core.components.cell.FullscreenErrorState
import com.illiarb.peek.uikit.core.components.popup.OpenInBrowserAction
import com.illiarb.peek.uikit.core.components.popup.ShareAction
import com.illiarb.peek.uikit.core.components.popup.SummarizeAction
import com.illiarb.peek.uikit.core.configuration.getScreenWidth
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_more
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import com.slack.circuit.overlay.OverlayEffect
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReaderScreen(
  modifier: Modifier,
  screen: ReaderScreen,
  state: ReaderScreenContract.State,
) {
  val eventSink = state.eventSink

  val progressColor = MaterialTheme.colorScheme.primary
  val progressSize = 8.dp
  val screenWidth = getScreenWidth()

  val contentScrollState = rememberScrollState()
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
  val percent = (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat()) * 100f
  val scrolledWidth = percent / 100f * screenWidth.value

  if (state.summaryShowing) {
    OverlayEffect(Unit) {
      showScreenOverlay<SummaryScreen, SummaryScreen.Result>(
        screen = SummaryScreen(url = screen.url, context = SummaryScreen.Context.READER),
        onDismiss = { SummaryScreen.Result.Close }
      )
      eventSink.invoke(Event.SummarizeResult)
    }
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        modifier = Modifier.backgroundProgressed(scrolledWidth, progressSize.value, progressColor),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationIconClicked) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        },
        actions = {
          ReaderActions(state)
        },
        title = {
          ReaderTitle(state.article)
        },
      )
    },
  ) { innerPadding ->
    val modifier = Modifier.fillMaxSize().padding(innerPadding)
    Box(modifier) {
      ReaderContent(state.article, contentScrollState, modifier) {
        eventSink.invoke(Event.ErrorRetryClicked)
      }
    }
  }
}

@Composable
private fun ReaderActions(state: ReaderScreenContract.State) {
  val eventSink = state.eventSink

  Box {
    IconButton(
      onClick = { eventSink.invoke(Event.TopBarMenuClicked) },
      enabled = state.article is Async.Content,
    ) {
      Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = stringResource(Res.string.acsb_action_more),
      )
    }

    DropdownMenu(
      expanded = state.topBarPopupShowing,
      onDismissRequest = { eventSink.invoke(Event.TopBarMenuDismissed) },
    ) {
      OpenInBrowserAction {
        eventSink.invoke(Event.TopBarOpenInBrowser)
      }
      SummarizeAction {
        eventSink.invoke(Event.TopBarSummarize)
      }
      ShareAction {
        eventSink.invoke(Event.TopBarShare)
      }
    }
  }
}

@Composable
private fun ReaderTitle(article: Async<Article>) {
  when (article) {
    is Async.Loading, is Async.Error -> TopAppBarTitleLoading()
    is Async.Content -> {
      Column {
        Text(
          text = article.content.title,
          style = MaterialTheme.typography.bodyLarge,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = article.content.url.url,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun ReaderContent(
  article: Async<Article>,
  contentScrollState: ScrollState,
  modifier: Modifier,
  onErrorClicked: () -> Unit,
) {
  when (article) {
    is Async.Loading -> ArticleReaderLoading()
    is Async.Error -> FullscreenErrorState(onActionClick = onErrorClicked)
    is Async.Content -> {
      WebView(
        url = article.content.url.url,
        modifier = modifier.fillMaxSize().verticalScroll(contentScrollState),
      )
    }
  }
}

private fun Modifier.backgroundProgressed(
  progress: Float,
  progressSize: Float,
  progressColor: Color,
): Modifier {
  return drawWithContent {
    drawContent()
    drawRect(
      brush = SolidColor(progressColor),
      size = Size(
        height = progressSize,
        width = progress.coerceIn(0f, this.size.width),
      ),
      topLeft = Offset(
        x = 0f,
        y = this.size.height - progress,
      )
    )
  }
}
