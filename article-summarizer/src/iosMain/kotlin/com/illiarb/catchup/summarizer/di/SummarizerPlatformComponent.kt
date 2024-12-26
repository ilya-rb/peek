package com.illiarb.catchup.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.catchup.summarizer.Database
import me.tatarka.inject.annotations.Provides

public actual interface SummarizerPlatformComponent {

  @Provides
  @SummarizerApi
  public fun provideSummarizerSqlDriver(): SqlDriver {
    return NativeSqliteDriver(
      schema = Database.Schema,
      name = "summarizer.db",
    )
  }
}