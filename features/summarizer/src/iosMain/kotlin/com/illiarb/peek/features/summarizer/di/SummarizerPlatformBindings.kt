package com.illiarb.peek.features.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.features.summarizer.Database
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
public actual object SummarizerPlatformBindings {

  @Provides
  @SummarizerApi
  public fun provideSummarizerSqlDriver(): SqlDriver {
    return NativeSqliteDriver(
      schema = Database.Schema,
      name = "summarizer.db",
    )
  }
}
