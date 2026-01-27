package com.illiarb.peek.features.reader

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.showScreenOverlay
import com.illiarb.peek.features.reader.ReaderScreenContract.Event
import com.illiarb.peek.uikit.core.atom.HtmlRenderer
import com.illiarb.peek.uikit.core.atom.WebView
import com.illiarb.peek.uikit.core.components.bottomsheet.ActionsBottomSheet
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.dropdown.OpenInBrowserAction
import com.illiarb.peek.uikit.core.components.dropdown.ShareAction
import com.illiarb.peek.uikit.core.components.dropdown.SummarizeAction
import com.illiarb.peek.uikit.core.components.dropdown.UiKitDropdown
import com.illiarb.peek.uikit.core.components.navigation.ProgressModel
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBarTitle
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBarTitleLoading
import com.illiarb.peek.uikit.core.model.ButtonModel
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_more
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
    RemoveBookmarkConfirmationSheet(
      onConfirm = {
        eventSink(Event.RemoveBookmarkResult(remove = true))
      },
      onCancel = {
        eventSink(Event.RemoveBookmarkResult(remove = false))
      },
    )
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      UiKitTopAppBar(
        progress = ProgressModel {
          if (contentScrollState.maxValue > 0) {
            (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat())
          } else {
            0f
          }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        onNavigationButtonClick = {
          eventSink.invoke(Event.NavigationIconClicked)
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
    ReaderContent(
      article = state.article,
      parsedContent = state.parsedContent,
      contentScrollState = contentScrollState,
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      onErrorClicked = { eventSink.invoke(Event.ErrorRetryClicked) },
    )
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

    UiKitDropdown(
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
private fun ReaderTitle(
  article: Async<Article>,
  modifier: Modifier = Modifier,
) {
  when (article) {
    is Async.Loading, is Async.Error -> UiKitTopAppBarTitleLoading(modifier)
    is Async.Content -> UiKitTopAppBarTitle(
      article.content.title,
      article.content.url.url,
      modifier = modifier,
    )
  }
}

@Composable
private fun ReaderContent(
  article: Async<Article>,
  parsedContent: Async<HtmlContent?>,
  contentScrollState: ScrollState,
  onErrorClicked: () -> Unit,
  modifier: Modifier,
) {
  when (article) {
    is Async.Loading -> ReaderLoading(modifier)
    is Async.Error -> ErrorEmptyState(modifier = modifier, onButtonClick = onErrorClicked)
    is Async.Content -> {
      when (parsedContent) {
        is Async.Loading -> ReaderLoading(modifier)
        is Async.Content -> {
          val content = parsedContent.content
          if (content != null) {
            HtmlRenderer(
              html = content.content,
              modifier = modifier
                .verticalScroll(contentScrollState)
                .padding(horizontal = 16.dp),
            )
          } else {
            WebView(
              url = article.content.url.url,
              modifier = modifier.verticalScroll(contentScrollState),
            )
          }
        }

        is Async.Error -> {
          WebView(
            url = article.content.url.url,
            modifier = modifier.verticalScroll(contentScrollState),
          )
        }
      }
    }
  }
}

@Composable
private fun RemoveBookmarkConfirmationSheet(
  onConfirm: () -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ActionsBottomSheet(
    modifier = modifier,
    title = stringResource(Res.string.reader_remove_bookmark_title),
    primaryButton = ButtonModel(
      text = stringResource(Res.string.reader_remove_bookmark_confirm),
      onClick = onConfirm,
    ),
    secondaryButton = ButtonModel(
      text = stringResource(Res.string.common_action_cancel),
      onClick = onCancel,
    ),
    onDismiss = onCancel,
  )
}
