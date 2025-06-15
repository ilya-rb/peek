package com.illiarb.peek.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.arch.ShareScreen
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.home.HomeScreen.BookmarkMessage
import com.illiarb.peek.features.home.HomeScreen.Event
import com.illiarb.peek.features.home.articles.ArticlesUiEvent
import com.illiarb.peek.features.home.bookmarks.BookmarksScreen
import com.illiarb.peek.features.home.overlay.TagFilterContract
import com.illiarb.peek.features.reader.ReaderScreen
import com.illiarb.peek.features.settings.SettingsScreen
import com.illiarb.peek.summarizer.ui.SummaryScreen
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
public class HomeScreenPresenterFactory(
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter.Factory {
  override fun create(
    screen: Screen,
    navigator: Navigator,
    context: CircuitContext
  ): Presenter<*>? {
    return when (screen) {
      is HomeScreen -> HomeScreenPresenter(peekApiService, navigator, messageDispatcher)
      else -> null
    }
  }
}

internal class HomeScreenPresenter(
  private val peekApiService: PeekApiService,
  private val navigator: Navigator,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<HomeScreen.State> {

  @Composable
  override fun present(): HomeScreen.State {
    val coroutineScope = rememberStableCoroutineScope()

    var filtersShowing by rememberRetained {
      mutableStateOf(value = false)
    }
    var selectedTags by rememberRetained {
      mutableStateOf(emptySet<Tag>())
    }
    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }
    var bookmarkMessage by rememberRetained {
      mutableStateOf<BookmarkMessage?>(value = null)
    }

    val newsSources by produceRetainedState<Async<ImmutableList<NewsSource>>>(Async.Loading) {
      peekApiService.collectAvailableSources().mapContent { sources ->
        sources.toImmutableList()
      }.collect {
        value = it
      }
    }

    var contentTriggers by rememberRetained {
      mutableStateOf(
        ContentTriggers(
          selectedNewsSourceIndex = 0,
          articleBookmarked = false,
          manualReloadTriggered = false,
        )
      )
    }

    val articles by produceRetainedState<Async<SnapshotStateList<Article>>>(
      initialValue = Async.Loading,
      key1 = newsSources,
      key2 = contentTriggers,
    ) {
      val source = when (val currentSources = newsSources) {
        is Async.Content -> currentSources.content.getOrNull(contentTriggers.selectedNewsSourceIndex)
        else -> null
      }

      if (source == null) {
        value = Async.Loading
      } else {
        peekApiService.collectLatestNewsFrom(source.kind)
          .mapContent { it.toMutableStateList() }
          .collect { value = it }
      }
    }

    return HomeScreen.State(
      newsSources = newsSources,
      selectedNewsSourceIndex = contentTriggers.selectedNewsSourceIndex,
      filtersShowing = filtersShowing,
      selectedTags = selectedTags.toImmutableSet(),
      allTags = articles.tags(),
      articleSummaryToShow = articleSummaryToShow,
      articles = articles.filteredBy(selectedTags),
      bookmarkMessage = bookmarkMessage,
      eventSink = { event ->
        when (event) {
          is Event.SettingsClicked -> navigator.goTo(SettingsScreen)
          is Event.ErrorRetryClicked -> {
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }

          is Event.FiltersClicked -> filtersShowing = true

          is Event.SummaryResult -> {
            articleSummaryToShow = null

            when (event.result) {
              is SummaryScreen.Result.Close -> Unit
              is SummaryScreen.Result.OpenInReader -> {
                navigator.goTo(ReaderScreen(event.result.url))
              }
            }
          }

          is Event.TagFilterResult -> {
            if (event.result is TagFilterContract.Output.Saved) {
              selectedTags = event.result.selectedTags
            }
            filtersShowing = false
          }

          is Event.TabClicked -> {
            val value = newsSources
            require(value is Async.Content<ImmutableList<NewsSource>>)

            contentTriggers = contentTriggers.copy(
              selectedNewsSourceIndex = value.content.indexOf(event.source)
            )
          }

          is Event.BookmarksClicked -> {
            navigator.goTo(BookmarksScreen)
          }

          is Event.BookmarkToastResult -> {
            bookmarkMessage = null
          }
        }
      },
      articlesEventSink = { event ->
        when (event) {
          is ArticlesUiEvent.ArticleClicked -> {
            navigator.goTo(ReaderScreen(event.item.url))
          }

          is ArticlesUiEvent.ArticleBookmarkClicked -> {
            coroutineScope.launch {
              val saved = !event.item.saved

              peekApiService.saveArticle(event.item.copy(saved = saved))
                .onSuccess {
                  contentTriggers = contentTriggers.copy(
                    articleBookmarked = !contentTriggers.articleBookmarked
                  )
                  // TODO: move to resources
                  val message = if (saved) "Added to bookmarks" else "Removed from bookmarks"

                  messageDispatcher.sendMessage(
                    MessageDispatcher.Message(
                      content = message,
                      type = MessageDispatcher.Message.MessageType.SUCCESS
                    )
                  )
                }
            }
          }

          is ArticlesUiEvent.ArticleSummarizeClicked -> {
            articleSummaryToShow = event.item
          }

          is ArticlesUiEvent.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(event.item.url.url))
          }

          is ArticlesUiEvent.ArticlesRefreshClicked -> {
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }
        }
      }
    )
  }

  private fun Async<SnapshotStateList<Article>>.tags(): Set<Tag> {
    return when (this) {
      is Async.Content -> content.flatMap(Article::tags)
        .filter { it.value.isNotEmpty() }
        .toSet()

      else -> emptySet()
    }
  }

  private fun Async<SnapshotStateList<Article>>.filteredBy(
    tags: Set<Tag>
  ): Async<SnapshotStateList<Article>> {
    if (tags.isEmpty()) {
      return this
    }

    return when (this) {
      is Async.Content -> copy(
        content = content
          .filter { article -> article.tags.any(tags::contains) }
          .toMutableStateList()
      )

      else -> this
    }
  }

  private data class ContentTriggers(
    val selectedNewsSourceIndex: Int,
    val articleBookmarked: Boolean,
    val manualReloadTriggered: Boolean,
  )
}
