package com.illiarb.catchup.summarizer.repository

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.AsyncDataStore
import com.illiarb.catchup.core.data.ConcurrentHashMapCache
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.summarizer.db.ArticlesSummaryDao
import com.illiarb.catchup.summarizer.di.SummarizerApi
import com.illiarb.catchup.summarizer.domain.ArticleSummary
import com.illiarb.catchup.summarizer.network.ApiConfig
import com.illiarb.catchup.summarizer.network.SummaryRequest
import com.illiarb.catchup.summarizer.network.SummaryResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class SummarizerRepository(
  @SummarizerApi private val httpClient: HttpClient,
  @SummarizerApi private val memoryCache: ConcurrentHashMapCache,
  private val summaryDao: ArticlesSummaryDao,
) {

  private val summaryStore = AsyncDataStore<String, ArticleSummary>(
    networkFetcher = { url ->
      val response = httpClient.post(
        path = "v1/chat/completions",
        requestBody = HttpClient.RequestBody.create(
          SummaryRequest(
            model = ApiConfig.MODEL,
            maxTokens = ApiConfig.MAX_TOKENS,
            messages = listOf(
              SummaryRequest.Message(
                content = "Summarize this $url",
                role = ApiConfig.ROLE,
              )
            )
          ),
        )
      ).getOrThrow()

      response.body<SummaryResponse>().asArticleSummary(url)
    },
    fromStorage = { url ->
      summaryDao.summaryByUrl(url).getOrNull()
    },
    intoStorage = { _, summary ->
      summaryDao.saveSummary(summary)
    },
    fromMemory = { url ->
      memoryCache.cache.get(url)
    },
    intoMemory = { url, summary ->
      memoryCache.cache.put(url, summary)
    },
  )

  public fun articleSummary(url: String): Flow<Async<ArticleSummary>> {
    return summaryStore.collect(url, AsyncDataStore.LoadStrategy.CacheOnly)
  }
}