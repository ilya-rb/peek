package com.illiarb.peek.features.tasks.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.features.tasks.TasksDatabase
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
internal actual object TasksPlatformBindings {

  @Provides
  @InternalApi
  @SingleIn(AppScope::class)
  fun provideSqlDriver(): SqlDriver =
    NativeSqliteDriver(
      schema = TasksDatabase.Schema,
      name = "tasks.db",
    )
}
