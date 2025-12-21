package com.illiarb.peek.api.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.api.Database
import com.illiarb.peek.core.arch.di.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
public actual object SqlDatabasePlatformBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideSqlDriver(): SqlDriver =
    NativeSqliteDriver(
      schema = Database.Schema,
      name = "peek.db",
    )
}
