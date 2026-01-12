package com.illiarb.peek.api.db.dao

import app.cash.sqldelight.coroutines.asFlow
import com.illiarb.peek.api.ArticleEntity
import com.illiarb.peek.api.Database
import com.illiarb.peek.api.di.InternalApi
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.logging.Logger
import com.illiarb.peek.core.types.Url
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Instant

@Inject
internal class ArticlesDao(
  @InternalApi private val db: Database,
  private val appDispatchers: AppDispatchers,
) {

  suspend fun articlesOfKind(sourceKind: NewsSourceKind): Result<List<Article>?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articlesOfKind(sourceKind.name).executeAsList()
          .map { entity -> entity.asArticle() }
          .takeIf { it.isNotEmpty() }
      }
    }
  }

  suspend fun articleByUrl(url: Url): Result<Article?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articleByUrl(url.url).executeAsOneOrNull()?.asArticle()
      }
    }
  }

  suspend fun saveArticle(article: Article): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.setSaved(
          saved = if (article.saved) 1L else 0L,
          url = article.url.url,
        )
        Unit
      }
    }.onFailure { error ->
      Logger.e(throwable = error)
    }
  }

  fun savedArticles(): Flow<List<Article>> {
    return db.articlesQueries.savedArticles().asFlow()
      .map { query ->
        query.executeAsList().map { entity ->
          entity.asArticle()
        }
      }
      .flowOn(appDispatchers.io)
  }

  suspend fun saveArticles(articles: List<Article>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.transactionWithResult {
          articles.forEach(::insertArticle)
        }
      }
    }
  }

  suspend fun savedArticlesUrls(): Result<List<Url>> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.savedArticlesUrls().executeAsList().map { Url(it) }
      }
    }
  }

  suspend fun deleteArticlesOlderThan(duration: Duration): Result<Unit> {
    return with(appDispatchers.io) {
      suspendRunCatching {
        val deleted =
          db.articlesQueries.deleteArticlesOlderThan(duration.inWholeMilliseconds).await()
        Logger.d { "Deleted $deleted stale articles" }
      }
    }
  }

  private fun insertArticle(article: Article) {
    db.articlesQueries.insert(
      url = article.url.url,
      title = article.title,
      kind = article.kind.name,
      date = article.date.toEpochMilliseconds(),
      saved = if (article.saved) 1L else 0L,
    )
  }

  private fun ArticleEntity.asArticle(): Article {
    return Article(
      url = Url(url),
      title = title,
      kind = NewsSourceKind.valueOf(kind),
      saved = saved == 1L,
      date = Instant.fromEpochMilliseconds(date),
    )
  }
}
