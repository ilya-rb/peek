package com.illiarb.catchup.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.ConcurrentHashMapCache
import com.illiarb.catchup.core.data.DefaultConcurrentHashMapCache
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.summarizer.BuildKonfig
import com.illiarb.catchup.summarizer.Database
import com.illiarb.catchup.summarizer.DefaultSummarizerService
import com.illiarb.catchup.summarizer.Summaries
import com.illiarb.catchup.summarizer.SummarizerService
import com.illiarb.catchup.summarizer.db.DatabaseAdapters
import com.illiarb.catchup.summarizer.network.authPlugin
import com.illiarb.catchup.summarizer.repository.SummarizerRepository
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Qualifier

public expect interface SummarizerPlatformComponent

public interface SummarizerComponent : SummarizerPlatformComponent {

  public val summarizerService: SummarizerService

  @SummarizerApi
  public val summarizerDatabase: Database

  @Provides
  @SummarizerApi
  public fun provideSummarizerHttpClient(
    factory: HttpClient.Factory,
  ): HttpClient {
    return factory.create(BuildKonfig.OPENAI_URL, plugins = listOf(authPlugin()))
  }

  @Provides
  @AppScope
  @SummarizerApi
  public fun provideSummarizerMemoryCache(): ConcurrentHashMapCache {
    return ConcurrentHashMapCache(DefaultConcurrentHashMapCache())
  }

  @Provides
  @AppScope
  public fun provideSummarizerService(repository: SummarizerRepository): SummarizerService {
    return DefaultSummarizerService(repository)
  }

  @Provides
  @SummarizerApi
  @AppScope
  public fun provideSummarizerDatabase(@SummarizerApi driver: SqlDriver): Database {
    return Database(
      driver = driver,
      summariesAdapter = Summaries.Adapter(created_atAdapter = DatabaseAdapters.instantAdapter),
    )
  }
}

@Qualifier
internal annotation class SummarizerApi