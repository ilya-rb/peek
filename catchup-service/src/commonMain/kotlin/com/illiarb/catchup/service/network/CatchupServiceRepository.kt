package com.illiarb.catchup.service.network

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.MemoryCache
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.network.dto.ArticleDto
import com.illiarb.catchup.service.network.dto.ArticlesResponse
import com.illiarb.catchup.service.network.dto.NewsSourcesResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface CatchupService {

  fun collectLatestNewsFrom(source: NewsSource): Flow<Async<List<Article>>>

  fun collectAvailableSources(): Flow<Async<Set<NewsSource>>>
}

@Inject
internal class CatchupServiceRepository(
  private val httpClient: HttpClient,
  private val networkConfig: NetworkConfig,
  private val memoryCache: MemoryCache<String>,
) : CatchupService {

  override fun collectLatestNewsFrom(source: NewsSource): Flow<Async<List<Article>>> =
    Async.fromFlow {
      val cached = memoryCache.get<List<Article>>(source.kind.key).orEmpty()

      cached.ifEmpty {
        httpClient.get(
          path = "news",
          parameters = mapOf("source" to source.kind.key)
        ).map {
          val response = it.body<ArticlesResponse>()
          response.articles.map(ArticleDto::asArticle)
        }.getOrThrow().also {
          memoryCache.put(source.kind.key, it)
        }
      }
    }

  override fun collectAvailableSources(): Flow<Async<Set<NewsSource>>> =
    Async.fromFlow {
      val cached = memoryCache.get<Set<NewsSource>>(KEY_AVAILABLE_SOURCES).orEmpty()

      cached.ifEmpty {
        httpClient.get(path = "supported_sources").map {
          val response = it.body<NewsSourcesResponse>()
          response.asNewsSourcesSet(networkConfig.apiUrl)
        }.getOrThrow().also {
          memoryCache.put(KEY_AVAILABLE_SOURCES, it)
        }
      }
    }

  companion object {
    const val KEY_AVAILABLE_SOURCES = "KEY_AVAILABLE_SOURCES"
  }
}
