package com.illiarb.catchup.service.db.dao

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.core.logging.Logger
import com.illiarb.catchup.service.ArticleEntity
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.ArticleContent
import com.illiarb.catchup.service.domain.NewsSource
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
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

  public suspend fun articleById(id: String): Result<Article?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articleById(id = id).executeAsOneOrNull()?.asArticle()
      }
    }
  }

  public suspend fun saveArticle(article: Article): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.setSaved(
          saved = if (article.saved) 1L else 0L,
          id = article.id,
        )
      }
    }.onFailure { error ->
      Logger.e(TAG, error)
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

  public suspend fun savedArticlesIds(): Result<List<String>> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.savedArticlesIds().executeAsList()
      }
    }
  }

  private fun insertArticle(article: Article) {
    db.articlesQueries.insert(
      id = article.id,
      title = article.title,
      shortSummary = article.shortSummary,
      link = article.link,
      tags = article.tags,
      source = article.source,
      createdAt = Clock.System.now(),
      authorName = article.authorName,
      content = article.content?.text,
      estimatedReadingTimeSeconds = article.content?.estimatedReadingTime,
      saved = if (article.saved) 1L else 0L,
    )
  }

  private fun ArticleEntity.asArticle(): Article {
    return Article(
      id = id,
      title = title,
      shortSummary = shortSummary,
      link = link,
      tags = tags.orEmpty(),
      source = source,
      authorName = authorName,
      content = content?.let {
        ArticleContent(
          text = it,
          estimatedReadingTime = requireNotNull(estimatedReadingTimeSeconds)
        )
      },
      saved = saved == 1L,
    )
  }

  private companion object {
    const val TAG = "ArticlesDao"
  }
}