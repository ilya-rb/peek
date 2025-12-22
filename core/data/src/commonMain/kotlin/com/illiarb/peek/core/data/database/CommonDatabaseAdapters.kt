package com.illiarb.peek.core.data.database

import app.cash.sqldelight.ColumnAdapter
import com.illiarb.peek.core.types.Url
import kotlin.time.Instant

/**
 * Not used for now
 * https://github.com/sqldelight/sqldelight/issues/5489
 */
@Suppress("unused")
public object CommonDatabaseAdapters {

  public val instantAdapter: ColumnAdapter<Instant, Long>
    get() = object : ColumnAdapter<Instant, Long> {
      override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochMilliseconds(databaseValue)
      }

      override fun encode(value: Instant): Long {
        return value.toEpochMilliseconds()
      }
    }

  public val urlAdapter: ColumnAdapter<Url, String>
    get() = object : ColumnAdapter<Url, String> {
      override fun decode(databaseValue: String): Url {
        return Url(databaseValue)
      }

      override fun encode(value: Url): String {
        return value.url
      }
    }
}
