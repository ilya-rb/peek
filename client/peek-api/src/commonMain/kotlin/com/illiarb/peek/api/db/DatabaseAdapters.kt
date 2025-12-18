package com.illiarb.peek.api.db

import app.cash.sqldelight.ColumnAdapter
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.domain.Tag
import com.illiarb.peek.core.types.Url
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.Instant
import kotlin.time.toDuration

internal object DatabaseAdapters {

  private const val TAGS_SEPARATOR = ","

  val tagsAdapter: ColumnAdapter<List<Tag>, String>
    get() = object : ColumnAdapter<List<Tag>, String> {
      override fun decode(databaseValue: String): List<Tag> {
        return databaseValue.split(TAGS_SEPARATOR).map(::Tag)
      }

      override fun encode(value: List<Tag>): String {
        return value.joinToString(separator = TAGS_SEPARATOR) { it.value }
      }
    }

  val kindAdapter: ColumnAdapter<NewsSourceKind, String>
    get() = object : ColumnAdapter<NewsSourceKind, String> {
      override fun decode(databaseValue: String): NewsSourceKind {
        return NewsSourceKind.valueOf(databaseValue)
      }

      override fun encode(value: NewsSourceKind): String {
        return value.name
      }
    }

  val instantAdapter: ColumnAdapter<Instant, Long>
    get() = object : ColumnAdapter<Instant, Long> {
      override fun decode(databaseValue: Long): Instant {
        return Instant.fromEpochMilliseconds(databaseValue)
      }

      override fun encode(value: Instant): Long {
        return value.toEpochMilliseconds()
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

  val durationAdapter: ColumnAdapter<Duration, Long>
    get() = object : ColumnAdapter<Duration, Long> {
      override fun decode(databaseValue: Long): Duration {
        return databaseValue.toDuration(DurationUnit.SECONDS)
      }

      override fun encode(value: Duration): Long {
        return value.inWholeSeconds
      }
    }
}