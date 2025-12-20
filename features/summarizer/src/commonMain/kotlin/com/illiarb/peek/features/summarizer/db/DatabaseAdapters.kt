package com.illiarb.peek.features.summarizer.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

internal object DatabaseAdapters {

  val instantAdapter: ColumnAdapter<Instant, Long>
    get() = object : ColumnAdapter<Instant, Long> {
      override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochMilliseconds(databaseValue)
      }

      override fun encode(value: Instant): Long {
        return value.toEpochMilliseconds()
      }
    }
}