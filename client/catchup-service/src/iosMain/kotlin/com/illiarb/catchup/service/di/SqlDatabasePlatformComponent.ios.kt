package com.illiarb.catchup.service.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.service.Database
import me.tatarka.inject.annotations.Provides

public actual interface SqlDatabasePlatformComponent {

  @AppScope
  @Provides
  public fun provideSqlDriver(): SqlDriver =
    NativeSqliteDriver(
      schema = Database.Schema,
      name = "catchup.db",
    )
}