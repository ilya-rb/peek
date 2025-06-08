package com.illiarb.peek.api.repository

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.NetworkConfig
import com.illiarb.peek.api.db.dao.NewsSourcesDao
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.api.network.dto.NewsSourcesDto
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class NewsSourcesRepository(
  private val httpClient: HttpClient,
  private val networkConfig: NetworkConfig,
  private val newsSourcesDao: NewsSourcesDao,
  private val memoryCache: ConcurrentHashMapCache,
) {

  private val supportedSourcesStore = AsyncDataStore<Unit, Set<NewsSource>>(
    networkFetcher = {
      httpClient.get(path = "supported_sources")
        .map {
          val response = it.body<NewsSourcesDto>()
          response.asNewsSourcesSet(networkConfig.apiUrl)
        }
        .getOrThrow()
    },
    fromMemory = {
      memoryCache().get(KEY_AVAILABLE_SOURCES)
    },
    intoMemory = { _, sources ->
      memoryCache().put(KEY_AVAILABLE_SOURCES, sources)
    },
    fromStorage = {
      newsSourcesDao.getAll().getOrNull()
    },
    intoStorage = { _, sources ->
      newsSourcesDao.insert(sources)
    },
  )

  public fun allSources(): Flow<Async<Set<NewsSource>>> {
    return supportedSourcesStore.collect(Unit, AsyncDataStore.LoadStrategy.CacheFirst)
  }

  public companion object {
    public const val KEY_AVAILABLE_SOURCES: String = "KEY_AVAILABLE_SOURCES"
  }
}