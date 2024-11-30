package com.illiarb.catchup.service.db.dao

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.db.DatabaseTransactionRunner
import com.illiarb.catchup.service.domain.Article
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
        db.articlesQueries.articlesBySource(
          source = sourceKind,
          mapper = { id, title, description, link, tags, source, _ ->
            Article(
              id = id,
              title = title,
              description = description,
              link = link,
              tags = tags.orEmpty(),
              source = source,
            )
          }
        ).executeAsList().takeIf {
          it.isNotEmpty()
        }
      }
    }
  }

  suspend fun articleById(id: String): Result<Article?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.articlesQueries.articleById(
          id = id,
          mapper = { id, title, description, link, tags, source, _ ->
            Article(
              id = id,
              title = title,
              description = description,
              link = link,
              tags = tags.orEmpty(),
              source = source,
            )
          },
        ).executeAsOneOrNull()
      }
    }
  }

  suspend fun deleteAndInsert(articles: List<Article>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        dbTransactionRunner {
          db.articlesQueries.deleteAll()

          articles.forEach { article ->
            db.articlesQueries.insert(
              id = article.id,
              title = article.title,
              description = article.description,
              link = article.link,
              tags = article.tags,
              source = article.source,
              created_at = Clock.System.now(),
            )
          }
        }
      }
    }
  }
}