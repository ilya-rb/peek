package com.illiarb.catchup.service.db.dao

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.service.ArticleEntity
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.db.DatabaseTransactionRunner
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.ArticleContent
import com.illiarb.catchup.service.domain.NewsSource
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

@Inject
class ArticlesDao(
  private val db: Database,
  private val dbTransactionRunner: DatabaseTransactionRunner,
  private val appDispatchers: AppDispatchers,
) {

  suspend fun articlesBySource(sourceKind: NewsSource.Kind): Result<List<Article>?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articlesBySource(source = sourceKind).executeAsList()
          .map { entity -> entity.asArticle() }
          .takeIf { it.isNotEmpty() }
      }
    }
  }

  suspend fun articleById(id: String): Result<Article?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articleById(id = id).executeAsOneOrNull()?.asArticle()
      }
    }
  }

  suspend fun deleteAndInsert(kind: NewsSource.Kind, articles: List<Article>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        dbTransactionRunner {
          db.articlesQueries.deleteBySource(kind)

          articles.forEach { article ->
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
            )
          }
        }
      }
    }
  }

  private fun ArticleEntity.asArticle(): Article {
    return Article(
      id,
      title,
      shortSummary,
      link,
      tags.orEmpty(),
      source,
      authorName,
      content?.let {
        ArticleContent(
          text = it,
          estimatedReadingTime = requireNotNull(estimatedReadingTimeSeconds)
        )
      }
    )
  }

}