package com.illiarb.catchup.service.repository

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.AsyncDataStore
import com.illiarb.catchup.core.data.MemoryCache
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.service.Database
import com.illiarb.catchup.service.db.dao.NewsSourcesDao
import com.illiarb.catchup.service.di.CatchupServiceComponent
import com.illiarb.catchup.service.di.CatchupServiceComponent.Cache
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.network.dto.NewsSourcesDto
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class NewsSourcesRepository(
  private val httpClient: HttpClient,
  private val networkConfig: NetworkConfig,
  private val newsSourcesDao: NewsSourcesDao,
  private val memoryCache: Cache,
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
      memoryCache.value.get(KEY_AVAILABLE_SOURCES)
    },
    intoMemory = { _, sources ->
      memoryCache.value.put(KEY_AVAILABLE_SOURCES, sources)
    },
    fromStorage = {
      newsSourcesDao.getAll().getOrNull()
    },
    intoStorage = { _, sources ->
      newsSourcesDao.insert(sources)
    },
  )

  fun allSources(): Flow<Async<Set<NewsSource>>> {
    return supportedSourcesStore.collect(Unit, AsyncDataStore.LoadStrategy.CacheFirst)
  }

  companion object {
    const val KEY_AVAILABLE_SOURCES = "KEY_AVAILABLE_SOURCES"
  }
}