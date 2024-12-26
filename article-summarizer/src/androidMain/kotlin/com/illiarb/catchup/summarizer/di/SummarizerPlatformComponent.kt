package com.illiarb.catchup.summarizer.di

import android.app.Application
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.illiarb.catchup.summarizer.Database
import me.tatarka.inject.annotations.Provides

public actual interface SummarizerPlatformComponent {

  @Provides
  @SummarizerApi
  public fun provideSummarizerSqlDriver(application: Application): SqlDriver {
    return AndroidSqliteDriver(
      schema = Database.Schema,
      context = application,
      name = "summarizer.db",
      callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          db.enableWriteAheadLogging()
        }
      }
    )
  }
}