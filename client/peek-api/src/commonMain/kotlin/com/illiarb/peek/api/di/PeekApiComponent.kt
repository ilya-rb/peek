package com.illiarb.peek.api.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.DefaultConcurrentHashMapCache
import com.illiarb.peek.api.ArticleEntity
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.Database
import com.illiarb.peek.api.DefaultPeekApiService
import com.illiarb.peek.api.NewsSourceEntity
import com.illiarb.peek.api.db.DatabaseAdapters
import com.illiarb.peek.api.repository.ArticlesRepository
import com.illiarb.peek.api.repository.NewsSourcesRepository
import me.tatarka.inject.annotations.Provides

public expect interface SqlDatabasePlatformComponent

public interface PeekApiComponent : SqlDatabasePlatformComponent {

  public val peekApiService: PeekApiService
  public val peekDatabase: Database

  @AppScope
  @Provides
  public fun provideMemoryCache(): ConcurrentHashMapCache =
    ConcurrentHashMapCache(DefaultConcurrentHashMapCache())

  @AppScope
  @Provides
  public fun providePeekApiService(
    articlesRepository: ArticlesRepository,
    newsSourcesRepository: NewsSourcesRepository,
  ): PeekApiService = DefaultPeekApiService(articlesRepository, newsSourcesRepository)

  @AppScope
  @Provides
  public fun provideDatabase(
    sqlDriver: SqlDriver
  ): Database = Database(
    driver = sqlDriver,
    articleEntityAdapter = ArticleEntity.Adapter(
      tagsAdapter = DatabaseAdapters.tagsAdapter,
      sourceAdapter = DatabaseAdapters.sourceAdapter,
      dateAdapter = DatabaseAdapters.instantAdapter,
      linkAdapter = DatabaseAdapters.urlAdapter,
    ),
    newsSourceEntityAdapter = NewsSourceEntity.Adapter(
      kindAdapter = DatabaseAdapters.sourceAdapter,
      image_urlAdapter = DatabaseAdapters.urlAdapter,
    )
  )
}
