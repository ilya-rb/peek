package com.illiarb.peek.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.summarizer.Database
import dev.zacsweers.metro.Provides

public actual interface SummarizerPlatformBindings {

  @Provides
  @SummarizerApi
  public fun provideSummarizerSqlDriver(): SqlDriver {
    return NativeSqliteDriver(
      schema = Database.Schema,
      name = "summarizer.db",
    )
  }
}