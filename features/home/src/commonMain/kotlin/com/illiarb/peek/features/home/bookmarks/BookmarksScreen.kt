package com.illiarb.peek.features.home.bookmarks

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.articles.ArticlesContent
import com.illiarb.peek.features.home.articles.ArticlesLoading
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.home.bookmarks.BookmarksScreenContract.Event
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.showScreenOverlay
import com.illiarb.peek.uikit.core.components.LocalLottieAnimation
import com.illiarb.peek.uikit.core.components.LottieAnimationType
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.cell.FullscreenErrorState
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import com.illiarb.peek.uikit.resources.bookmarks_empty
import com.illiarb.peek.uikit.resources.bookmarks_screen_title
import com.slack.circuit.overlay.OverlayEffect
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookmarksScreen(
  state: BookmarksScreenContract.State,
  ignored: Modifier = Modifier,
) {
  val eventSink = state.eventSink

  if (state.articleSummaryToShow != null) {
    OverlayEffect(Unit) {
      showScreenOverlay(
        screen = SummaryScreen(state.articleSummaryToShow.url, SummaryScreen.Context.HOME),
        onDismiss = { SummaryScreen.Result.Close }
      )
      eventSink.invoke(Event.SummaryCloseClicked)
    }
  }

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(stringResource(Res.string.bookmarks_screen_title))
        },
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationButtonClicked) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Default.ArrowBack,
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        },
        actions = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
          )
        }
      )
    },
    content = { innerPadding ->
      BookmarksContent(state, innerPadding)
    },
  )
}

@Composable
private fun BookmarksContent(
  state: BookmarksScreenContract.State,
  contentPadding: PaddingValues,
) {
  val articlesEventSink = state.articlesEventSink
  val eventSink = state.eventSink

  AnimatedContent(
    contentKey = { state.articles.stateKey() },
    targetState = state.articles,
    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
  ) { targetState ->
    when (targetState) {
      is Async.Error -> {
        FullscreenErrorState(Modifier.padding(contentPadding)) {
          eventSink.invoke(Event.ErrorRetryClicked)
        }
      }

      is Async.Loading -> {
        ArticlesLoading(contentPadding = contentPadding)
      }

      is Async.Content -> {
        if (targetState.content.isEmpty()) {
          BookmarksEmpty(contentPadding = contentPadding) {
            articlesEventSink.invoke(ArticlesUi.ArticlesRefreshClicked)
          }
        } else {
          ArticlesContent(
            contentPadding = contentPadding,
            articles = targetState.content,
            eventSink = articlesEventSink,
          )
        }
      }
    }
  }
}

@Composable
private fun BookmarksEmpty(
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  onActionClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(contentPadding),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    EmptyState(
      title = stringResource(Res.string.bookmarks_empty),
      buttonText = null,
      onButtonClick = onActionClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(shape = RoundedCornerShape(size = 24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
      LocalLottieAnimation(
        modifier = Modifier.size(200.dp),
        animationType = LottieAnimationType.ARTICLES_EMPTY,
      )
    }
  }
}
