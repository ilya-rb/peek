package com.illiarb.peek.features.tasks.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
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
  fun provideSqlDriver(context: Context): SqlDriver =
    AndroidSqliteDriver(
      schema = TasksDatabase.Schema,
      context = context,
      name = "tasks.db",
      callback = object : AndroidSqliteDriver.Callback(TasksDatabase.Schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          db.enableWriteAheadLogging()
        }
      },
    )
}
