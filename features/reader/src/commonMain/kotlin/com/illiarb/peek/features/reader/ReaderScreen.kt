package com.illiarb.peek.features.reader

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.showOverlay
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
import com.illiarb.peek.uikit.resources.common_action_cancel
import com.illiarb.peek.uikit.resources.reader_remove_bookmark_confirm
import com.illiarb.peek.uikit.resources.reader_remove_bookmark_title
import com.slack.circuit.overlay.OverlayEffect
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReaderScreen(
  modifier: Modifier,
  screen: ReaderScreen,
  state: ReaderScreenContract.State,
) {
  val eventSink = state.eventSink

  val contentScrollState = rememberScrollState()
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

  val screenWidth = getScreenWidth()
  val progress = if (contentScrollState.maxValue > 0) {
    (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat()) * screenWidth.value
  } else {
    0f
  }

  val isScrolledToEnd by remember {
    derivedStateOf {
      contentScrollState.maxValue > 0 && contentScrollState.value >= contentScrollState.maxValue
    }
  }

  LaunchedEffect(isScrolledToEnd) {
    if (isScrolledToEnd) {
      eventSink.invoke(Event.ScrolledToEnd)
    }
  }

  if (state.showSummary) {
    OverlayEffect(Unit) {
      showScreenOverlay<SummaryScreen, SummaryScreen.Result>(
        screen = SummaryScreen(url = screen.url, context = SummaryScreen.Context.READER),
        onDismiss = { SummaryScreen.Result.Close }
      )
      eventSink.invoke(Event.SummarizeResult)
    }
  }

  if (state.showRemoveBookmarkConfirmation) {
    OverlayEffect(Unit) {
      val result = showOverlay(
        input = Unit,
        content = { _, navigator ->
          RemoveBookmarkConfirmationSheet(
            onConfirm = { navigator.finish(true) },
            onCancel = { navigator.finish(false) },
          )
        },
        onDismiss = { false },
      )
      eventSink.invoke(Event.RemoveBookmarkResult(result))
    }
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        modifier = Modifier.withProgressLine(progress),
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
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      ReaderContent(state.article, contentScrollState) {
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
      expanded = state.showTopBarPopup,
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
  onErrorClicked: () -> Unit,
) {
  when (article) {
    is Async.Loading -> ArticleReaderLoading()
    is Async.Error -> FullscreenErrorState(onActionClick = onErrorClicked)
    is Async.Content -> {
      WebView(
        url = article.content.url.url,
        modifier = Modifier.fillMaxSize().verticalScroll(contentScrollState),
      )
    }
  }
}

@Composable
private fun Modifier.withProgressLine(progress: Float): Modifier {
  val progressColor = MaterialTheme.colorScheme.primary
  val progressSize = 8.dp.value

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
        y = this.size.height - progressSize,
      )
    )
  }
}

@Composable
private fun RemoveBookmarkConfirmationSheet(
  onConfirm: () -> Unit,
  onCancel: () -> Unit,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = stringResource(Res.string.reader_remove_bookmark_title),
      style = MaterialTheme.typography.titleMedium,
    )
    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      OutlinedButton(
        onClick = onCancel,
        modifier = Modifier.weight(1f),
      ) {
        Text(stringResource(Res.string.common_action_cancel))
      }
      Button(
        onClick = onConfirm,
        modifier = Modifier.weight(1f),
      ) {
        Text(stringResource(Res.string.reader_remove_bookmark_confirm))
      }
    }
  }
}
