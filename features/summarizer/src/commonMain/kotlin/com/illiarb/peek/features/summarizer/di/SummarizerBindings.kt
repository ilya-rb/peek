package com.illiarb.peek.features.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.DefaultConcurrentHashMapCache
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.NetworkConfig
import com.illiarb.peek.core.network.TimeoutConfig
import com.illiarb.peek.features.summarizer.Database
import com.illiarb.peek.features.summarizer.DefaultSummarizerService
import com.illiarb.peek.features.summarizer.SummarizerService
import com.illiarb.peek.features.summarizer.network.authPlugin
import com.illiarb.peek.features.summarizer.repository.SummarizerRepository
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn
import kotlin.time.Duration.Companion.seconds

@BindingContainer(includes = [SummarizerPlatformBindings::class])
public object SummarizerBindings {

  @Provides
  @InternalApi
  public fun provideSummarizerNetworkConfig(): NetworkConfig {
    return NetworkConfig(
      timeouts = TimeoutConfig.default(read = 40.seconds.inWholeMilliseconds),
    )
  }

  @Provides
  @InternalApi
  public fun provideSummarizerHttpClient(
    config: NetworkConfig,
    factory: HttpClient.Factory,
  ): HttpClient {
    return factory.create(
      config = config,
      plugins = listOf(authPlugin()),
    )
  }

  @Provides
  @SingleIn(AppScope::class)
  @InternalApi
  public fun provideSummarizerMemoryCache(): ConcurrentHashMapCache {
    return ConcurrentHashMapCache(DefaultConcurrentHashMapCache())
  }

  @Provides
  @SingleIn(AppScope::class)
  public fun provideSummarizerService(repository: SummarizerRepository): SummarizerService {
    return DefaultSummarizerService(repository)
  }

  @Provides
  @InternalApi
  @SingleIn(AppScope::class)
  public fun provideSummarizerDatabase(@InternalApi driver: SqlDriver): Database {
    return Database(driver)
  }
}

@BindingContainer
public expect object SummarizerPlatformBindings

@Qualifier
internal annotation class InternalApi
