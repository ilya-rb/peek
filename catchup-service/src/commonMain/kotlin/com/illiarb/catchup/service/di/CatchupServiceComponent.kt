package com.illiarb.catchup.service.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.HashMapCache
import com.illiarb.catchup.core.data.MemoryCache
import com.illiarb.catchup.service.ArticleEntity
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.DefaultCatchupService
import com.illiarb.catchup.service.NewsSourceEntity
import com.illiarb.catchup.service.db.DatabaseAdapters
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
  public fun provideDatabase(
    sqlDriver: SqlDriver
  ): Database = Database(
    driver = sqlDriver,
    articleEntityAdapter = ArticleEntity.Adapter(
      tagsAdapter = DatabaseAdapters.tagsAdapter,
      sourceAdapter = DatabaseAdapters.sourceAdapter,
      createdAtAdapter = DatabaseAdapters.instantAdapter,
      linkAdapter = DatabaseAdapters.urlAdapter,
      estimatedReadingTimeSecondsAdapter = DatabaseAdapters.durationAdapter,
    ),
    newsSourceEntityAdapter = NewsSourceEntity.Adapter(
      kindAdapter = DatabaseAdapters.sourceAdapter,
      image_urlAdapter = DatabaseAdapters.urlAdapter,
    )
  )

  public data class Cache(val value: MemoryCache<String>)
}
