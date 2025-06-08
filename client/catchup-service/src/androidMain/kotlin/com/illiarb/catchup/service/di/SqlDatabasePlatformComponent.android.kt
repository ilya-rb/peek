package com.illiarb.catchup.service.di

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.service.Database
import me.tatarka.inject.annotations.Provides

public actual interface SqlDatabasePlatformComponent {

  @AppScope
  @Provides
  public fun provideSqlDriver(application: Application): SqlDriver =
    AndroidSqliteDriver(
      schema = Database.Schema,
      context = application,
      name = "catchup.db",
      callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          db.enableWriteAheadLogging()
        }
      },
    )
}