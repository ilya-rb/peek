package com.illiarb.peek.features.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.features.summarizer.Database
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
internal actual object SummarizerPlatformBindings {

  @Provides
  @InternalApi
  fun provideSummarizerSqlDriver(): SqlDriver {
    return NativeSqliteDriver(
      schema = Database.Schema,
      name = "summarizer.db",
    )
  }
}
