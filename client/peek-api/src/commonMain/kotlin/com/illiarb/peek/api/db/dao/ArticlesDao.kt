package com.illiarb.peek.api.db.dao

import com.illiarb.peek.api.ArticleEntity
import com.illiarb.peek.api.Database
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.logging.Logger
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class ArticlesDao(
  private val db: Database,
  private val appDispatchers: AppDispatchers,
) {

  public suspend fun articlesBySource(sourceKind: NewsSource.Kind): Result<List<Article>?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articlesBySource(source = sourceKind).executeAsList()
          .map { entity -> entity.asArticle() }
          .takeIf { it.isNotEmpty() }
      }
    }
  }

  public suspend fun articleByUrl(url: Url): Result<Article?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articleByUrl(url).executeAsOneOrNull()?.asArticle()
      }
    }
  }

  public suspend fun saveArticle(article: Article): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.setSaved(
          saved = if (article.saved) 1L else 0L,
          url = article.url,
        )
        Unit
      }
    }.onFailure { error ->
      Logger.e(throwable = error)
    }
  }

  public suspend fun savedArticles(): Result<List<Article>> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.savedArticles()
          .executeAsList()
          .map { entity -> entity.asArticle() }
      }
    }
  }

  public suspend fun saveArticles(articles: List<Article>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.transactionWithResult {
          articles.forEach(::insertArticle)
        }
      }
    }
  }

  public suspend fun savedArticlesUrls(): Result<List<Url>> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.savedArticlesUrls().executeAsList()
      }
    }
  }

  private fun insertArticle(article: Article) {
    db.articlesQueries.insert(
      url = article.url,
      title = article.title,
      tags = article.tags,
      source = article.source,
      date = article.date,
      saved = if (article.saved) 1L else 0L,
    )
  }

  private fun ArticleEntity.asArticle(): Article {
    return Article(
      url = url,
      title = title,
      tags = tags.orEmpty(),
      source = source,
      saved = saved == 1L,
      date = date,
    )
  }
}