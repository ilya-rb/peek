package com.illiarb.peek.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.arch.message.MessageDispatcher
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.home.HomeScreenContract.Event
import com.illiarb.peek.features.home.HomeScreenContract.State.BookmarkMessage
import com.illiarb.peek.features.home.articles.ArticlesUi
import com.illiarb.peek.features.home.tags.TagFilterOverlay
import com.illiarb.peek.features.navigation.map.BookmarksScreen
import com.illiarb.peek.features.navigation.map.ReaderScreen
import com.illiarb.peek.features.navigation.map.SettingsScreen
import com.illiarb.peek.features.navigation.map.ShareScreen
import com.illiarb.peek.features.navigation.map.SummaryScreen
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class HomeScreenPresenter(
  private val navigator: Navigator,
  private val peekApiService: PeekApiService,
  private val messageDispatcher: MessageDispatcher,
) : Presenter<HomeScreenContract.State> {

  private val newsSources: ImmutableList<NewsSourceKind> by lazy {
    peekApiService.getAvailableSources().toImmutableList()
  }

  @Composable
  override fun present(): HomeScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()

    var filtersShowing by rememberRetained {
      mutableStateOf(value = false)
    }
    var selectedTags by rememberRetained {
      mutableStateOf(emptyList<Tag>())
    }
    var articleSummaryToShow by rememberRetained {
      mutableStateOf<Article?>(value = null)
    }
    var bookmarkMessage by rememberRetained {
      mutableStateOf<BookmarkMessage?>(value = null)
    }
    var contentTriggers by rememberRetained {
      mutableStateOf(
        HomeScreenContract.ContentTriggers(
          selectedNewsSourceIndex = 0,
          articleBookmarked = false,
          manualReloadTriggered = false,
        )
      )
    }

    val articles by produceRetainedState<Async<ImmutableList<Article>>>(
      initialValue = Async.Loading,
      key1 = contentTriggers,
    ) {
      val source = newsSources[contentTriggers.selectedNewsSourceIndex]

      peekApiService.collectLatestNewsFrom(source)
        .mapContent { it.toImmutableList() }
        .collect { value = it }
    }

    val allTags by derivedStateOf { articles.tags().toImmutableList() }
    val articlesFiltered by derivedStateOf { articles.filteredBy(selectedTags) }

    return HomeScreenContract.State(
      newsSources = newsSources,
      selectedNewsSourceIndex = contentTriggers.selectedNewsSourceIndex,
      filtersShowing = filtersShowing,
      selectedTags = selectedTags.toImmutableList(),
      allTags = allTags,
      articleSummaryToShow = articleSummaryToShow,
      articles = articlesFiltered,
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
            if (event.result is TagFilterOverlay.Output.Saved) {
              selectedTags = event.result.selectedTags
            }
            filtersShowing = false
          }

          is Event.TabClicked -> {
            contentTriggers = contentTriggers.copy(
              selectedNewsSourceIndex = newsSources.indexOf(event.source)
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
          is ArticlesUi.ArticleClicked -> {
            navigator.goTo(ReaderScreen(event.item.url))
          }

          is ArticlesUi.ArticleBookmarkClicked -> {
            coroutineScope.launch {
              val saved = !event.item.saved

              peekApiService.saveArticle(event.item.copy(saved = saved))
                .onSuccess {
                  contentTriggers = contentTriggers.copy(
                    articleBookmarked = !contentTriggers.articleBookmarked
                  )
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

          is ArticlesUi.ArticleSummarizeClicked -> {
            articleSummaryToShow = event.item
          }

          is ArticlesUi.ArticleShareClicked -> {
            navigator.goTo(ShareScreen(event.item.url))
          }

          is ArticlesUi.ArticlesRefreshClicked -> {
            contentTriggers = contentTriggers.copy(
              manualReloadTriggered = !contentTriggers.manualReloadTriggered
            )
          }
        }
      }
    )
  }

  private fun Async<ImmutableList<Article>>.tags(): Set<Tag> {
    return when (this) {
      is Async.Content -> content.flatMap(Article::tags)
        .filter { it.value.isNotEmpty() }
        .toSet()

      else -> emptySet()
    }
  }

  private fun Async<ImmutableList<Article>>.filteredBy(
    tags: List<Tag>
  ): Async<ImmutableList<Article>> {
    if (tags.isEmpty()) {
      return this
    }

    return when (this) {
      is Async.Content -> copy(
        content = content
          .filter { article -> article.tags.any(tags::contains) }
          .toImmutableList()
      )

      else -> this
    }
  }
}
