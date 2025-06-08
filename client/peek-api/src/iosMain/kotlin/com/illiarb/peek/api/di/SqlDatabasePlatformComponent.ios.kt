package com.illiarb.peek.api.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.api.Database
import me.tatarka.inject.annotations.Provides

public actual interface SqlDatabasePlatformComponent {

  @AppScope
  @Provides
  public fun provideSqlDriver(): SqlDriver =
    NativeSqliteDriver(
      schema = Database.Schema,
      name = "peek.db",
    )
}