package com.illiarb.peek.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.HomeScreen.Event
import com.illiarb.peek.features.home.articles.ArticlesContent
import com.illiarb.peek.features.home.articles.ArticlesEmpty
import com.illiarb.peek.features.home.articles.ArticlesLoading
import com.illiarb.peek.features.home.overlay.TagFilterContract
import com.illiarb.peek.features.home.overlay.showTagFilterOverlay
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.illiarb.peek.summarizer.ui.showSummaryOverlay
import com.illiarb.peek.uikit.core.components.HorizontalList
import com.illiarb.peek.uikit.core.components.TextSwitcher
import com.illiarb.peek.uikit.core.components.cell.FullscreenErrorState
import com.illiarb.peek.uikit.core.components.cell.SelectableCircleAvatar
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_bookmarks
import com.illiarb.peek.uikit.resources.acsb_action_filter
import com.illiarb.peek.uikit.resources.acsb_action_settings
import com.illiarb.peek.uikit.resources.home_screen_title
import com.illiarb.peek.uikit.resources.service_dou_name
import com.illiarb.peek.uikit.resources.service_ft_name
import com.illiarb.peek.uikit.resources.service_hacker_news_name
import com.slack.circuit.overlay.OverlayEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
internal fun HomeScreen(state: HomeScreen.State, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink
  val articlesEventSink = state.articlesEventSink

  val bottomSheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
  val bottomBarBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
  val bottomBarAlpha = 1 - bottomBarBehavior.state.collapsedFraction

  val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  val hazeState = rememberHazeState()
  val hazeStyle = HazeMaterials.thin(MaterialTheme.colorScheme.surface)

  val articleFilterVisible by derivedStateOf { state.allTags.isNotEmpty() }

  when {
    state.filtersShowing -> {
      OverlayEffect(Unit) {
        val result = showTagFilterOverlay(
          TagFilterContract.Input(
            allTags = state.allTags.take(5).toSet(),
            selectedTags = state.selectedTags,
            containerColor = bottomSheetContainerColor,
          ),
        )
        eventSink.invoke(Event.TagFilterResult(result))
      }
    }

    state.articleSummaryToShow != null -> {
      OverlayEffect(Unit) {
        val result = showSummaryOverlay(
          SummaryScreen(
            state.articleSummaryToShow.url,
            context = SummaryScreen.Context.HOME,
          ),
        )
        eventSink.invoke(Event.SummaryResult(result))
      }
    }
  }

  Scaffold(
    modifier = Modifier
      .nestedScroll(bottomBarBehavior.nestedScrollConnection)
      .nestedScroll(topBarBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        scrollBehavior = topBarBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          scrolledContainerColor = Color.Transparent,
        ),
        modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle),
        title = {
          val selectedSource = state.newsSources[state.selectedNewsSourceIndex]
          val name = when (selectedSource) {
            NewsSourceKind.Dou -> stringResource(Res.string.service_dou_name)
            NewsSourceKind.HackerNews -> stringResource(Res.string.service_hacker_news_name)
            NewsSourceKind.Ft -> stringResource(Res.string.service_ft_name)
          }
          TextSwitcher(
            first = stringResource(Res.string.home_screen_title),
            second = name,
            containerHeightDp = TopAppBarDefaults.TopAppBarExpandedHeight.value.toInt(),
            switchEvery = 5.seconds,
          )
        },
        actions = {
          IconButton(onClick = { eventSink.invoke(Event.BookmarksClicked) }) {
            Icon(
              imageVector = Icons.Filled.Bookmarks,
              contentDescription = stringResource(Res.string.acsb_action_bookmarks),
            )
          }
          IconButton(onClick = { eventSink.invoke(Event.SettingsClicked) }) {
            Icon(
              imageVector = Icons.Filled.Settings,
              contentDescription = stringResource(Res.string.acsb_action_settings),
            )
          }
        },
      )
    },
    bottomBar = {
      BottomAppBar(
        modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle),
        scrollBehavior = bottomBarBehavior,
        containerColor = Color.Transparent,
        actions = {
          NewsSourcesContent(
            newsSources = state.newsSources,
            selectedTabIndex = state.selectedNewsSourceIndex,
            onTabClick = { eventSink.invoke(Event.TabClicked(it)) },
            modifier = Modifier
              .padding(start = 16.dp)
              .alpha(bottomBarAlpha)
          )
        },
        floatingActionButton = {
          AnimatedVisibility(
            visible = articleFilterVisible,
            enter = scaleIn(),
            exit = scaleOut(),
          ) {
            IconButton(
              colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
              ),
              onClick = { eventSink.invoke(Event.FiltersClicked) },
              modifier = Modifier.alpha(bottomBarAlpha),
            ) {
              Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = stringResource(Res.string.acsb_action_filter),
              )
            }
          }
        }
      )
    },
  ) { innerPadding ->
    AnimatedContent(
      contentKey = { state.articlesStateKey() },
      targetState = state.articles,
      transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
      when (targetState) {
        is Async.Error -> {
          FullscreenErrorState(Modifier.padding(innerPadding)) {
            eventSink.invoke(Event.ErrorRetryClicked)
          }
        }

        is Async.Loading -> {
          ArticlesLoading(contentPadding = innerPadding)
        }

        is Async.Content -> {
          if (targetState.content.isEmpty()) {
            ArticlesEmpty(contentPadding = innerPadding, eventSink = articlesEventSink)
          } else {
            ArticlesContent(
              modifier = Modifier.hazeSource(state = hazeState),
              contentPadding = innerPadding,
              articles = targetState.content,
              eventSink = articlesEventSink,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NewsSourcesContent(
  modifier: Modifier = Modifier,
  newsSources: ImmutableList<NewsSourceKind>,
  selectedTabIndex: Int,
  onTabClick: (NewsSourceKind) -> Unit,
) {
  HorizontalList(
    modifier = modifier,
    items = newsSources,
    keyProvider = { _, source -> source.name },
    itemContent = { index, source ->
      SelectableCircleAvatar(
        imageUrl = "https://picsum.photos/200/300",
        selected = index == selectedTabIndex,
        fallbackText = source.name.uppercase(),
        onClick = {
          onTabClick.invoke(source)
        }
      )
    },
  )
}