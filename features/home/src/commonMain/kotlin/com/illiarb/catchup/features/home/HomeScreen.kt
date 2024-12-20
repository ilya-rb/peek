package com.illiarb.catchup.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.HomeScreenContract.Event
import com.illiarb.catchup.features.home.filters.FiltersOverlayModel
import com.illiarb.catchup.features.home.filters.showFiltersOverlay
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.uikit.core.components.ArticleCell
import com.illiarb.catchup.uikit.core.components.ArticleLoadingCell
import com.illiarb.catchup.uikit.core.components.ErrorStateKind
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import com.illiarb.catchup.uikit.core.components.FullscreenState
import com.illiarb.catchup.uikit.core.components.HorizontalList
import com.illiarb.catchup.uikit.core.components.LocalLottieAnimation
import com.illiarb.catchup.uikit.core.components.LottieAnimationType
import com.illiarb.catchup.uikit.core.components.SelectableCircleAvatar
import com.illiarb.catchup.uikit.core.components.SelectableCircleAvatarLoading
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_filter
import com.illiarb.catchup.uikit.resources.acsb_action_saved
import com.illiarb.catchup.uikit.resources.acsb_action_settings
import com.illiarb.catchup.uikit.resources.home_articles_empty_action
import com.illiarb.catchup.uikit.resources.home_articles_empty_title
import com.illiarb.catchup.uikit.resources.service_dou_name
import com.illiarb.catchup.uikit.resources.service_hacker_news_name
import com.illiarb.catchup.uikit.resources.service_irish_times_name
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import kotlinx.collections.immutable.ImmutableList
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class HomeScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is HomeScreen -> {
        ui<HomeScreenContract.State> { state, _ ->
          HomeScreen(state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(state: HomeScreenContract.State) {
  ContentWithOverlays {
    when {
      state.filtersShowing -> {
        OverlayEffect(Unit) {
          val result = showFiltersOverlay(
            FiltersOverlayModel(state.articleTags, state.selectedTags)
          )
          state.eventSink.invoke(Event.FiltersResult(result))
        }
      }
    }

    val eventSink = state.eventSink
    val bottomBarBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
      modifier = Modifier
        .nestedScroll(bottomBarBehavior.nestedScrollConnection)
        .nestedScroll(topBarBehavior.nestedScrollConnection),
      topBar = {
        TopAppBar(
          scrollBehavior = topBarBehavior,
          title = {
            when (val content = state.tabs) {
              is Async.Loading -> Unit
              is Async.Error -> Unit
              is Async.Content -> {
                val selected = content.content[state.selectedTabIndex]
                Text(
                  text = selected.serviceName(),
                  style = MaterialTheme.typography.titleLarge,
                )
              }
            }
          },
          actions = {
            IconButton(onClick = { eventSink.invoke(Event.SettingsClicked) }) {
              Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(Res.string.acsb_action_settings),
              )
            }
            IconButton(onClick = { eventSink.invoke(Event.SavedClicked) }) {
              Icon(
                contentDescription = stringResource(Res.string.acsb_action_saved),
                imageVector = if (state.onlyBookmarkedShowing) {
                  Icons.Filled.Bookmark
                } else {
                  Icons.Filled.BookmarkBorder
                },
              )
            }
          },
        )
      },
      bottomBar = {
        BottomAppBar(
          scrollBehavior = bottomBarBehavior,
          actions = {
            AnimatedContent(
              targetState = state.tabs,
              transitionSpec = { fadeIn().togetherWith(fadeOut()) }
            ) { targetState ->
              when (targetState) {
                is Async.Loading -> {
                  TabsLoading(Modifier.padding(start = 16.dp))
                }

                is Async.Content -> {
                  TabsContent(
                    modifier = Modifier.padding(start = 16.dp),
                    tabs = targetState.content,
                    selectedTabIndex = state.selectedTabIndex,
                    eventSink = eventSink,
                  )
                }

                else -> Unit
              }
            }
          },
          floatingActionButton = {
            val hasFilters = state.articleTags.isNotEmpty()
            if (hasFilters) {
              FloatingActionButton(
                onClick = {
                  eventSink.invoke(Event.FiltersClicked)
                }
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
        contentKey = { it is Async.Content },
        targetState = state.content,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
      ) { targetState ->
        when {
          targetState is Async.Error || state.tabs is Async.Error -> {
            FullscreenErrorState(Modifier.padding(innerPadding), ErrorStateKind.UNKNOWN) {
              eventSink.invoke(Event.ErrorRetryClicked)
            }
          }

          targetState is Async.Loading -> {
            ArticlesLoading(contentPadding = innerPadding)
          }

          targetState is Async.Content -> {
            if (targetState.content.isEmpty()) {
              ArticlesEmpty(contentPadding = innerPadding) {
                eventSink.invoke(Event.ErrorRetryClicked)
              }
            } else {
              ArticlesContent(
                contentPadding = innerPadding,
                articles = targetState.content,
                eventSink = eventSink,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TabsLoading(modifier: Modifier = Modifier) {
  LazyRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    items(
      count = 3,
      itemContent = { index ->
        SelectableCircleAvatarLoading(selected = index == 0)
      },
    )
  }
}

@Composable
private fun TabsContent(
  modifier: Modifier = Modifier,
  tabs: ImmutableList<HomeScreenContract.Tab>,
  selectedTabIndex: Int,
  eventSink: (Event) -> Unit,
) {
  HorizontalList(
    modifier = modifier,
    items = tabs,
    itemContent = { index, tab ->
      SelectableCircleAvatar(
        imageUrl = tab.imageUrl,
        selected = index == selectedTabIndex,
        fallbackText = tab.source.kind.key.uppercase(),
        onClick = { eventSink.invoke(Event.TabClicked(tab.source)) }
      )
    },
  )
}

@Composable
private fun ArticlesLoading(modifier: Modifier = Modifier, contentPadding: PaddingValues) {
  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(
      count = 5,
      itemContent = {
        ArticleLoadingCell()
      }
    )
  }
}

@Composable
private fun ArticlesEmpty(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  onRefreshClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(contentPadding),
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
        animationType = LottieAnimationType.ARTICLES_EMPTY,
      )
    }
  }
}

@Composable
private fun ArticlesContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  articles: SnapshotStateList<Article>,
  eventSink: (Event) -> Unit,
) {
  LazyColumn(modifier, contentPadding = contentPadding) {
    items(
      items = articles,
      key = { article -> article.id },
      itemContent = { article ->
        ArticleCell(
          title = article.title,
          author = article.authorName,
          caption = article.tags.firstOrNull()?.value.orEmpty(),
          saved = article.saved,
          onClick = {
            eventSink.invoke(Event.ArticleClicked(article))
          },
          onBookmarkClick = {
            eventSink.invoke(Event.ArticleBookmarkClicked(article))
          },
          modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
        )
      }
    )
  }
}

@Composable
private fun HomeScreenContract.Tab.serviceName(): String {
  return when (source.kind) {
    NewsSource.Kind.IrishTimes -> stringResource(Res.string.service_irish_times_name)
    NewsSource.Kind.HackerNews -> stringResource(Res.string.service_hacker_news_name)
    NewsSource.Kind.Dou -> stringResource(Res.string.service_dou_name)
    NewsSource.Kind.Unknown -> ""
  }
}
