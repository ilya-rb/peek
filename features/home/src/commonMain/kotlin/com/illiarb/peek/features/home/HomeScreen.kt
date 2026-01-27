package com.illiarb.peek.features.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.home.HomeScreenContract.Event
import com.illiarb.peek.features.home.HomeScreenContract.NewsSource
import com.illiarb.peek.features.home.HomeScreenContract.TasksIndicator
import com.illiarb.peek.features.home.articles.ArticlesContent
import com.illiarb.peek.features.home.articles.ArticlesEmpty
import com.illiarb.peek.features.home.articles.ArticlesLoading
import com.illiarb.peek.features.navigation.map.ServicesScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.illiarb.peek.features.navigation.map.showScreenOverlay
import com.illiarb.peek.uikit.core.atom.AvatarState
import com.illiarb.peek.uikit.core.atom.BorderState
import com.illiarb.peek.uikit.core.atom.ContentSwitcher
import com.illiarb.peek.uikit.core.atom.HorizontalList
import com.illiarb.peek.uikit.core.atom.SelectableCircleAvatar
import com.illiarb.peek.uikit.core.atom.bordered
import com.illiarb.peek.uikit.core.components.cell.ErrorEmptyState
import com.illiarb.peek.uikit.core.components.date.DateFormats
import com.illiarb.peek.uikit.core.components.navigation.NavigationButton
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBar
import com.illiarb.peek.uikit.core.components.navigation.UiKitTopAppBarTitle
import com.illiarb.peek.uikit.core.theme.UiKitColors
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_action_bookmarks
import com.illiarb.peek.uikit.resources.acsb_action_reorder_services
import com.illiarb.peek.uikit.resources.acsb_action_settings
import com.illiarb.peek.uikit.resources.acsb_action_tasks
import com.illiarb.peek.uikit.resources.home_screen_articles_updated
import com.illiarb.peek.uikit.resources.home_screen_title
import com.slack.circuit.overlay.OverlayEffect
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun HomeScreen(state: HomeScreenContract.State, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink

  val bottomBarBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
  val topBarBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  val topBarFullyVisible by remember {
    derivedStateOf {
      topBarBehavior.state.collapsedFraction == 0f
    }
  }

  val hazeState = rememberHazeState()
  val hazeStyle = HazeMaterials.thin(MaterialTheme.colorScheme.surface)

  state.articleSummaryToShow?.let { article ->
    OverlayEffect(article) {
      val result = showScreenOverlay(
        SummaryScreen(article.url, SummaryScreen.Context.HOME),
        onDismiss = { SummaryScreen.Result.Close as SummaryScreen.Result },
      )
      eventSink.invoke(Event.SummaryResult(result))
    }
  }

  state.servicesOrderToShow?.let {
    OverlayEffect(ServicesScreen) {
      showScreenOverlay(
        ServicesScreen,
        onDismiss = { ServicesScreen.Result }
      )
      eventSink.invoke(Event.ReorderServicesClosed)
    }
  }

  PullToRefreshBox(
    isRefreshing = state.contentRefreshing,
    onRefresh = { eventSink.invoke(Event.RefreshTriggered) },
  ) {
    Scaffold(
      modifier = modifier
        .nestedScroll(bottomBarBehavior.nestedScrollConnection)
        .nestedScroll(topBarBehavior.nestedScrollConnection),
      topBar = {
        UiKitTopAppBar(
          scrollBehavior = topBarBehavior,
          navigationButton = NavigationButton.None,
          colors = TopAppBarDefaults.topAppBarColors(scrolledContainerColor = Color.Transparent),
          modifier = Modifier.hazeEffect(state = hazeState, style = hazeStyle),
          title = {
            TopBarTitle(
              state = state,
              textSwitcherEnabled = topBarFullyVisible,
            )
          },
          actions = {
            TopBarActions(state.tasksIndicator, eventSink)
          },
        )
      },
      bottomBar = {
        BottomBar(state, hazeState, hazeStyle, bottomBarBehavior, eventSink)
      },
      content = { innerPadding ->
        ScreenContent(state, innerPadding, hazeState)
      }
    )
  }
}

@Composable
private fun TopBarTitle(
  state: HomeScreenContract.State,
  textSwitcherEnabled: Boolean,
) {
  if (state.newsSources.isEmpty()) {
    return
  }
  val selectedSource = state.newsSources[state.selectedNewsSourceIndex]

  ContentSwitcher(
    first = { modifier ->
      UiKitTopAppBarTitle(stringResource(Res.string.home_screen_title), modifier = modifier)
    },
    second = { modifier ->
      UiKitTopAppBarTitle(
        modifier = modifier,
        title = selectedSource.name,
        subtitle = state.articlesLastUpdatedTime?.let { time ->
          stringResource(
            Res.string.home_screen_articles_updated,
            DateFormats.formatTimestamp(Clock.System.now() - time).lowercase(),
          )
        },
      )
    },
    containerHeightDp = TopAppBarDefaults.TopAppBarExpandedHeight.value.toInt(),
    switchEvery = 5.seconds,
    enabled = textSwitcherEnabled,
  )
}

@Composable
private fun TopBarActions(
  tasksIndicator: TasksIndicator,
  eventSink: (Event) -> Unit,
) {
  IconButton(onClick = { eventSink.invoke(Event.TasksClicked) }) {
    val borderState = when (tasksIndicator) {
      TasksIndicator.None -> BorderState.None
      TasksIndicator.HasIncompleteTasks -> BorderState.Pulsating(UiKitColors.orange)
      TasksIndicator.AllTasksCompleted -> BorderState.Static(UiKitColors.green)
    }
    Icon(
      modifier = Modifier.bordered(borderState),
      imageVector = Icons.Filled.CheckCircle,
      contentDescription = stringResource(Res.string.acsb_action_tasks),
    )
  }
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
}

@Composable
private fun BottomBar(
  state: HomeScreenContract.State,
  hazeState: HazeState,
  hazeStyle: HazeStyle,
  bottomBarBehavior: BottomAppBarScrollBehavior,
  eventSink: (Event) -> Unit,
) {
  val bottomBarAlpha = 1 - bottomBarBehavior.state.collapsedFraction

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
      IconButton(
        onClick = { eventSink.invoke(Event.ReorderServicesClicked) },
        colors = IconButtonDefaults.iconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
      ) {
        Icon(
          imageVector = Icons.Filled.Reorder,
          tint = MaterialTheme.colorScheme.primary,
          contentDescription = stringResource(Res.string.acsb_action_reorder_services),
        )
      }
    }
  )
}

@Composable
private fun ScreenContent(
  state: HomeScreenContract.State,
  innerPadding: PaddingValues,
  hazeState: HazeState,
) {
  val eventSink = state.eventSink
  val articlesEventSink = state.articlesEventSink

  AnimatedContent(
    contentKey = { state.articles.stateKey() },
    targetState = state.articles,
    transitionSpec = { fadeIn().togetherWith(fadeOut()) },
  ) { targetState ->
    when (targetState) {
      is Async.Error -> {
        ErrorEmptyState(modifier = Modifier.padding(innerPadding)) {
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

@Composable
private fun NewsSourcesContent(
  modifier: Modifier = Modifier,
  newsSources: ImmutableList<NewsSource>,
  selectedTabIndex: Int,
  onTabClick: (NewsSource) -> Unit,
) {
  HorizontalList(
    modifier = modifier,
    items = newsSources,
    keyProvider = { _, source -> source.name },
    itemContent = { index, source ->
      SelectableCircleAvatar(
        image = source.icon,
        state = if (index == selectedTabIndex) {
          AvatarState.Selected
        } else {
          AvatarState.Unselected
        },
        onClick = {
          onTabClick.invoke(source)
        }
      )
    },
  )
}

