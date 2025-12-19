package com.illiarb.peek.summarizer.repository

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.summarizer.BuildKonfig
import com.illiarb.peek.summarizer.db.ArticlesSummaryDao
import com.illiarb.peek.summarizer.di.SummarizerApi
import com.illiarb.peek.summarizer.domain.ArticleSummary
import com.illiarb.peek.summarizer.network.ApiConfig
import com.illiarb.peek.summarizer.network.SummaryRequest
import com.illiarb.peek.summarizer.network.SummaryResponse
import dev.zacsweers.metro.Inject
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow

@Inject
public class SummarizerRepository(
  @SummarizerApi private val httpClient: HttpClient,
  @SummarizerApi private val memoryCache: ConcurrentHashMapCache,
  private val summaryDao: ArticlesSummaryDao,
) {

  private val summaryStore = AsyncDataStore<String, ArticleSummary>(
    networkFetcher = { url ->
      val response = httpClient.post(
        url = "${BuildKonfig.OPENAI_URL}/v1/chat/completions",
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