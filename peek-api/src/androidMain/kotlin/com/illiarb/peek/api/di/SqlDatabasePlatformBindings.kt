package com.illiarb.peek.api.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.illiarb.peek.api.Database
import com.illiarb.peek.core.arch.di.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
public actual object SqlDatabasePlatformBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideSqlDriver(context: Context): SqlDriver =
    AndroidSqliteDriver(
      schema = Database.Schema,
      context = context,
      name = "peek.db",
      callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          db.enableWriteAheadLogging()
        }
      },
    )
}
