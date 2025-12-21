package com.illiarb.peek.features.summarizer.di

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.illiarb.peek.features.summarizer.Database
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
public actual object SummarizerPlatformBindings {

  @Provides
  @SummarizerApi
  public fun provideSummarizerSqlDriver(context: Context): SqlDriver {
    return AndroidSqliteDriver(
      schema = Database.Schema,
      context = context,
      name = "summarizer.db",
      callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
        override fun onConfigure(db: SupportSQLiteDatabase) {
          db.enableWriteAheadLogging()
        }
      }
    )
  }
}
