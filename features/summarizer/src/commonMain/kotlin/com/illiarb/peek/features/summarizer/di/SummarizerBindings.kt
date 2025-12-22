package com.illiarb.peek.features.summarizer.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.MemoryCache
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

@BindingContainer(
  includes = [
    SummarizerBindingsInternal::class,
    SummarizerPlatformBindings::class,
  ]
)
public object SummarizerBindings

@BindingContainer
internal object SummarizerBindingsInternal {

  @Provides
  fun provideSummarizerService(repository: SummarizerRepository): SummarizerService {
    return DefaultSummarizerService(repository)
  }

  @Provides
  @InternalApi
  fun provideSummarizerNetworkConfig(): NetworkConfig {
    return NetworkConfig(
      timeouts = TimeoutConfig(read = 30.seconds.inWholeSeconds),
    )
  }

  @Provides
  @InternalApi
  fun provideSummarizerHttpClient(
    @InternalApi config: NetworkConfig,
    factory: HttpClient.Factory,
  ): HttpClient {
    return factory.create(
      config = config,
      plugins = listOf(authPlugin()),
    )
  }

  @Provides
  @InternalApi
  fun provideSummarizerMemoryCache(): MemoryCache<String> {
    return ConcurrentHashMapCache()
  }

  @Provides
  @InternalApi
  @SingleIn(AppScope::class)
  fun provideSummarizerDatabase(@InternalApi driver: SqlDriver): Database {
    return Database(driver)
  }
}

@BindingContainer
internal expect object SummarizerPlatformBindings

@Qualifier
internal annotation class InternalApi
