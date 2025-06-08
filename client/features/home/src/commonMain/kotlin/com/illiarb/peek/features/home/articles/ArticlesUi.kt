package com.illiarb.peek.features.home.articles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.uikit.core.components.LocalLottieAnimation
import com.illiarb.peek.uikit.core.components.LottieAnimationType
import com.illiarb.peek.uikit.core.components.cell.ArticleCell
import com.illiarb.peek.uikit.core.components.cell.ArticleLoadingCell
import com.illiarb.peek.uikit.core.components.cell.EmptyState
import com.illiarb.peek.uikit.core.components.text.DateFormats
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.home_articles_empty_action
import com.illiarb.peek.uikit.resources.home_articles_empty_title
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

internal sealed interface ArticlesUiEvent {
  data class ArticleClicked(val item: Article) : ArticlesUiEvent
  data class ArticleBookmarkClicked(val item: Article) : ArticlesUiEvent
  data class ArticleSummarizeClicked(val item: Article) : ArticlesUiEvent
  data class ArticleShareClicked(val item: Article) : ArticlesUiEvent
  data object ArticlesRefreshClicked : ArticlesUiEvent
}

@Composable
internal fun ArticlesLoading(modifier: Modifier = Modifier, contentPadding: PaddingValues) {
  LazyColumn(modifier = modifier, contentPadding = contentPadding) {
    items(
      count = 5,
      itemContent = {
        ArticleLoadingCell()

        HorizontalDivider(
          color = DividerDefaults.color.copy(alpha = 0.5f)
        )
      }
    )
  }
}

@Composable
internal fun ArticlesEmpty(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  eventSink: (ArticlesUiEvent) -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(contentPadding),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    EmptyState(
      title = stringResource(Res.string.home_articles_empty_title),
      buttonText = stringResource(Res.string.home_articles_empty_action),
      onButtonClick = { eventSink.invoke(ArticlesUiEvent.ArticlesRefreshClicked) },
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
internal fun ArticlesContent(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues,
  articles: SnapshotStateList<Article>,
  eventSink: (ArticlesUiEvent) -> Unit,
) {
  LazyColumn(modifier, contentPadding = contentPadding) {
    items(
      items = articles,
      key = { article -> article.id },
      itemContent = { article ->
        ArticleCell(
          modifier = Modifier.animateItem(),
          title = article.title,
          caption = article.date.toLocalDateTime(TimeZone.UTC).format(DateFormats.default),
          subtitle = article.tags.firstOrNull()?.value.orEmpty(),
          saved = article.saved,
          onClick = {
            eventSink.invoke(ArticlesUiEvent.ArticleClicked(article))
          },
          onBookmarkClick = {
            eventSink.invoke(ArticlesUiEvent.ArticleBookmarkClicked(article))
          },
          onSummarizeClick = {
            eventSink.invoke(ArticlesUiEvent.ArticleSummarizeClicked(article))
          },
          onShareClick = {
            eventSink.invoke(ArticlesUiEvent.ArticleShareClicked(article))
          },
        )
        HorizontalDivider(
          color = DividerDefaults.color.copy(alpha = 0.5f)
        )
      }
    )
  }
}