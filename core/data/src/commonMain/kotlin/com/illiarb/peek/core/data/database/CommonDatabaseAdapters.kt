package com.illiarb.peek.core.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Instant

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
}
