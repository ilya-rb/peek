package com.illiarb.peek.api.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.api.ArticleEntity
import com.illiarb.peek.api.Database
import com.illiarb.peek.api.DefaultPeekApiService
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.datasource.RssNewsDataSource
import com.illiarb.peek.api.db.DatabaseAdapters
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.repository.ArticlesRepository
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.DefaultConcurrentHashMapCache
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.NetworkConfig
import com.illiarb.peek.core.network.TimeoutConfig
import com.illiarb.peek.core.types.Url
import com.prof18.rssparser.RssParser
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer(
  includes = [
    RssParserBindings::class,
    SqlDatabasePlatformBindings::class,
  ]
)
public object PeekApiBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideMemoryCache(): ConcurrentHashMapCache =
    ConcurrentHashMapCache(DefaultConcurrentHashMapCache())

  @Provides
  public fun providePeekApiNetworkConfig(): NetworkConfig {
    return NetworkConfig(
      timeouts = TimeoutConfig.default(),
    )
  }

  @Provides
  public fun provideHttpClient(
    config: NetworkConfig,
    factory: HttpClient.Factory,
  ): HttpClient {
    return factory.create(config)
  }

  @Provides
  @SingleIn(AppScope::class)
  public fun providePeekApiService(
    articlesRepository: ArticlesRepository,
    newsDataSources: Set<NewsDataSource>,
  ): PeekApiService = DefaultPeekApiService(articlesRepository, newsDataSources)

  @Provides
  @SingleIn(AppScope::class)
  public fun provideDatabase(
    sqlDriver: SqlDriver
  ): Database = Database(
    driver = sqlDriver,
    articleEntityAdapter = ArticleEntity.Adapter(
      tagsAdapter = DatabaseAdapters.tagsAdapter,
      kindAdapter = DatabaseAdapters.kindAdapter,
      dateAdapter = DatabaseAdapters.instantAdapter,
      urlAdapter = DatabaseAdapters.urlAdapter,
    ),
  )

  @Provides
  @IntoSet
  public fun provideHackerNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(
      rssParser,
      Url("https://hnrss.org/frontpage"),
      NewsSourceKind.HackerNews,
    )
  }

  @Provides
  @IntoSet
  public fun provideFtNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(
      rssParser,
      Url("https://www.ft.com/myft/following/f2fb2051-3462-4df1-b761-667021a25113.rss"),
      NewsSourceKind.Ft,
    )
  }

  @Provides
  @IntoSet
  public fun provideDouNewsDataSource(rssParser: RssParser): NewsDataSource {
    return RssNewsDataSource(
      rssParser,
      Url("https://dou.ua/forums/feed/tag/tech"),
      NewsSourceKind.Dou,
    )
  }
}

@BindingContainer
public expect object SqlDatabasePlatformBindings

@BindingContainer
public expect object RssParserBindings
