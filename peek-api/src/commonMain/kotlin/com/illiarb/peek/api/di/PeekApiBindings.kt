package com.illiarb.peek.api.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.api.BuildKonfig
import com.illiarb.peek.api.Database
import com.illiarb.peek.api.DefaultPeekApiService
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.datasource.RssNewsDataSource
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.repository.ArticlesRepository
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.MemoryCache
import com.illiarb.peek.core.types.Url
import com.prof18.rssparser.RssParser
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn

@BindingContainer(
  includes = [
    PeekApiBindingsInternal::class,
    RssParserBindings::class,
    SqlDatabasePlatformBindings::class,
  ]
)
public object PeekApiBindings

@BindingContainer
internal object PeekApiBindingsInternal {

  @Provides
  fun providePeekApiService(
    articlesRepository: ArticlesRepository,
    newsDataSources: Set<NewsDataSource>,
  ): PeekApiService {
    return DefaultPeekApiService(articlesRepository, newsDataSources)
  }

  @Provides
  @InternalApi
  fun provideMemoryCache(): MemoryCache<String> {
    return ConcurrentHashMapCache()
  }

  @Provides
  @SingleIn(AppScope::class)
  @InternalApi
  fun provideDatabase(sqlDriver: SqlDriver): Database {
    return Database(sqlDriver)
  }

  @Provides
  @IntoSet
  fun provideHackerNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(rssParser, Url(BuildKonfig.SERVICE_HN), NewsSourceKind.HackerNews)
  }

  @Provides
  @IntoSet
  fun provideFtNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(rssParser, Url(BuildKonfig.SERVICE_FT), NewsSourceKind.Ft)
  }

  @Provides
  @IntoSet
  fun provideDouNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(rssParser, Url(BuildKonfig.SERVICE_DOU), NewsSourceKind.Dou)
  }
}

@BindingContainer
internal expect object SqlDatabasePlatformBindings

@BindingContainer
internal expect object RssParserBindings

@Qualifier
internal annotation class InternalApi
