package com.illiarb.peek.api.db

import app.cash.sqldelight.ColumnAdapter
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.types.Url

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

  val urlAdapter: ColumnAdapter<Url, String>
    get() = object : ColumnAdapter<Url, String> {
      override fun decode(databaseValue: String): Url {
        return Url(databaseValue)
      }

      override fun encode(value: Url): String {
        return value.url
      }
    }
}
