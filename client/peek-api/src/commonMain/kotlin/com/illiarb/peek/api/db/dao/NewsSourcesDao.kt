package com.illiarb.peek.api.db.dao

import com.illiarb.peek.api.Database
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class NewsSourcesDao(
  private val db: Database,
  private val appDispatchers: AppDispatchers,
) {

  public suspend fun getAll(): Result<Set<NewsSource>?> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.news_sourcesQueries
          .all(mapper = { kind, icon, name -> NewsSource(kind, icon, name) })
          .executeAsList()
          .toSet()
          .takeIf { it.isNotEmpty() }
      }
    }
  }

  public suspend fun insert(sources: Set<NewsSource>): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.transactionWithResult {
          sources.forEach { source ->
            db.news_sourcesQueries.insert(source.kind, source.icon, source.name)
          }
        }
      }
    }
  }

  public suspend fun delete(): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching { db.news_sourcesQueries.delete() }
    }
  }
}