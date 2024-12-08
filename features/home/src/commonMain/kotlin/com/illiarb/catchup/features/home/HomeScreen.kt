package com.illiarb.catchup.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.home.debug.showDebugOverlay
import com.illiarb.catchup.features.home.filters.FiltersOverlayModel
import com.illiarb.catchup.features.home.filters.showFiltersOverlay
import com.illiarb.catchup.service.domain.Article
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
import com.illiarb.catchup.uikit.resources.home_articles_empty_action
import com.illiarb.catchup.uikit.resources.home_articles_empty_title
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
class Factory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is HomeScreenContract.HomeScreen -> {
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
internal fun HomeScreen(state: HomeScreenContract.State) {
  ContentWithOverlays {
    when {
      state.debugMenuShowing -> {
        OverlayEffect(Unit) {
          showDebugOverlay()
          state.eventSink.invoke(HomeScreenContract.Event.DebugMenuClosed)
        }
      }

      state.filtersShowing -> {
        OverlayEffect(Unit) {
          val selectedTags = showFiltersOverlay(
            FiltersOverlayModel(state.articleTags, state.selectedTags)
          )
          state.eventSink.invoke(HomeScreenContract.Event.TagsSelected(selectedTags))
        }
      }
    }

    val eventSink = state.eventSink
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
      modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      topBar = {
        TopAppBar(
          scrollBehavior = scrollBehavior,
          colors = TopAppBarDefaults.topAppBarColors(),
          title = {},
          actions = {
            AnimatedContent(
              targetState = state.tabs,
              transitionSpec = { fadeIn().togetherWith(fadeOut()) }
            ) { targetState ->
              when (targetState) {
                is Async.Loading -> {
                  TabsLoading(Modifier.padding(start = 16.dp, bottom = 16.dp))
                }

                is Async.Content -> {
                  TabsContent(
                    modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                    tabs = targetState.content,
                    selectedTabIndex = state.selectedTabIndex,
                    eventSink = eventSink,
                  )
                }

                else -> Unit
              }
            }

            Spacer(modifier = Modifier.weight(1f))

            val hasFilters = state.articleTags.isNotEmpty()
            if (hasFilters) {
              MenuItem(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 8.dp),
                icon = Icons.Filled.FilterList,
              ) {
                eventSink.invoke(HomeScreenContract.Event.FiltersClick)
              }
            }
          },
        )
      }
    ) { innerPadding ->
      AnimatedContent(
        modifier = Modifier.padding(innerPadding),
        targetState = state.content,
        transitionSpec = { fadeIn().togetherWith(fadeOut()) }
      ) { targetState ->
        when {
          targetState is Async.Error || state.tabs is Async.Error -> {
            FullscreenErrorState(ErrorStateKind.UNKNOWN) {
              eventSink.invoke(HomeScreenContract.Event.ErrorRetryClick)
            }
          }

          targetState is Async.Loading -> {
            ArticlesLoading()
          }

          targetState is Async.Content -> {
            if (targetState.content.isEmpty()) {
              ArticlesEmpty {
                eventSink.invoke(HomeScreenContract.Event.ErrorRetryClick)
              }
            } else {
              ArticlesContent(
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
fun TabsLoading(modifier: Modifier = Modifier) {
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
fun TabsContent(
  modifier: Modifier = Modifier,
  tabs: ImmutableList<HomeScreenContract.Tab>,
  selectedTabIndex: Int,
  eventSink: (HomeScreenContract.Event) -> Unit,
) {
  HorizontalList(
    modifier = modifier,
    items = tabs,
    itemContent = { index, tab ->
      SelectableCircleAvatar(
        imageUrl = tab.imageUrl,
        selected = index == selectedTabIndex,
        fallbackText = tab.source.kind.key.uppercase(),
        onClick = { eventSink.invoke(HomeScreenContract.Event.TabClicked(tab.source)) }
      )
    },
  )
}

@Composable
fun ArticlesLoading(modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) {
    items(
      count = 5,
      itemContent = {
        ArticleLoadingCell()
      }
    )
  }
}

@Composable
fun ArticlesEmpty(
  modifier: Modifier = Modifier,
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
        animationType = LottieAnimationType.ARTICLES_EMPTY,
      )
    }
  }
}

@Composable
fun ArticlesContent(
  modifier: Modifier = Modifier,
  articles: List<Article>,
  eventSink: (HomeScreenContract.Event) -> Unit,
) {
  LazyColumn(modifier = modifier) {
    items(
      items = articles,
      key = { article -> article.id },
      itemContent = { article ->
        ArticleCell(
          title = article.title,
          caption = article.tags.firstOrNull()?.value.orEmpty(),
          onClick = {
            eventSink.invoke(HomeScreenContract.Event.ArticleClicked(article))
          },
        )
      }
    )
  }
}

@Composable
fun MenuItem(
  modifier: Modifier = Modifier,
  icon: ImageVector,
  onClick: () -> Unit,
) {
  IconButton(
    modifier = modifier,
    onClick = { onClick() }
  ) {
    Icon(
      imageVector = icon,
      tint = MaterialTheme.colorScheme.onSurface,
      contentDescription = null,
    )
  }
}


