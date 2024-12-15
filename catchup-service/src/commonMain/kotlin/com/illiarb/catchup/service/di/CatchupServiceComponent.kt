package com.illiarb.catchup.service.di

import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.HashMapCache
import com.illiarb.catchup.core.data.MemoryCache
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.DefaultCatchupService
import com.illiarb.catchup.service.db.DatabaseFactory
import com.illiarb.catchup.service.repository.ArticlesRepository
import com.illiarb.catchup.service.repository.NewsSourcesRepository
import me.tatarka.inject.annotations.Provides

public expect interface SqlDatabasePlatformComponent

public interface CatchupServiceComponent : SqlDatabasePlatformComponent {

  @AppScope
  @Provides
  public fun provideMemoryCache(): Cache = Cache(HashMapCache())

  @AppScope
  @Provides
  public fun provideCatchupService(
    articlesRepository: ArticlesRepository,
    newsSourcesRepository: NewsSourcesRepository,
  ): CatchupService = DefaultCatchupService(articlesRepository, newsSourcesRepository)

  @AppScope
  @Provides
  public fun provideDatabase(factory: DatabaseFactory): Database = factory.create()

  public data class Cache(val value: MemoryCache<String>)
}
