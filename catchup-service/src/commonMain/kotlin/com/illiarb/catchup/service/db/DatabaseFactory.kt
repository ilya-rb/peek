package com.illiarb.catchup.service.db

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.catchup.service.ArticleEntity
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.NewsSourceEntity
import me.tatarka.inject.annotations.Inject

@Inject
class DatabaseFactory(private val driver: SqlDriver) {

  fun create(): Database {
    return Database(
      driver = driver,
      articleEntityAdapter = ArticleEntity.Adapter(
        tagsAdapter = DatabaseAdapters.tagsAdapter,
        sourceAdapter = DatabaseAdapters.sourceAdapter,
        created_atAdapter = DatabaseAdapters.instantAdapter,
        linkAdapter = DatabaseAdapters.urlAdapter,
      ),
      newsSourceEntityAdapter = NewsSourceEntity.Adapter(
        kindAdapter = DatabaseAdapters.sourceAdapter,
        image_urlAdapter = DatabaseAdapters.urlAdapter,
      )
    )
  }
}