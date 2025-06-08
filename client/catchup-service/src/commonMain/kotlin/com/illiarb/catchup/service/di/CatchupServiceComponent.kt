package com.illiarb.catchup.service.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.ConcurrentHashMapCache
import com.illiarb.catchup.core.data.DefaultConcurrentHashMapCache
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

  public val catchupService: CatchupService
  public val catchupDatabase: Database

  @AppScope
  @Provides
  public fun provideMemoryCache(): ConcurrentHashMapCache =
    ConcurrentHashMapCache(DefaultConcurrentHashMapCache())

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
      dateAdapter = DatabaseAdapters.instantAdapter,
      linkAdapter = DatabaseAdapters.urlAdapter,
    ),
    newsSourceEntityAdapter = NewsSourceEntity.Adapter(
      kindAdapter = DatabaseAdapters.sourceAdapter,
      image_urlAdapter = DatabaseAdapters.urlAdapter,
    )
  )
}
