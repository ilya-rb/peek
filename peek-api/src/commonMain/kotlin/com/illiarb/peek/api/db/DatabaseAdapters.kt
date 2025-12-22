package com.illiarb.peek.api.db

import app.cash.sqldelight.ColumnAdapter
import com.illiarb.peek.api.domain.NewsSourceKind

/**
 * Not used for now
 * https://github.com/sqldelight/sqldelight/issues/5489
 */
@Suppress("unused")
internal object DatabaseAdapters {

  val kindAdapter: ColumnAdapter<NewsSourceKind, String>
    get() = object : ColumnAdapter<NewsSourceKind, String> {
      override fun decode(databaseValue: String): NewsSourceKind {
        return NewsSourceKind.valueOf(databaseValue)
      }

      override fun encode(value: NewsSourceKind): String {
        return value.name
      }
    }
}
