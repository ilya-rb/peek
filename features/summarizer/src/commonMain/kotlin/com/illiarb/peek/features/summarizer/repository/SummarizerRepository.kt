package com.illiarb.peek.features.summarizer.repository

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.MemoryCache
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.summarizer.BuildKonfig
import com.illiarb.peek.features.summarizer.db.ArticlesSummaryDao
import com.illiarb.peek.features.summarizer.di.InternalApi
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import com.illiarb.peek.features.summarizer.network.ApiConfig
import com.illiarb.peek.features.summarizer.network.SummaryRequest
import com.illiarb.peek.features.summarizer.network.SummaryResponse
import dev.zacsweers.metro.Inject
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow

@Inject
public class SummarizerRepository(
  @InternalApi private val httpClient: HttpClient,
  @InternalApi private val memoryCache: MemoryCache<String>,
  private val summaryDao: ArticlesSummaryDao,
) {

  private val summaryStore = AsyncDataStore<Url, ArticleSummary>(
    networkFetcher = { url ->
      val response = httpClient.post(
        url = "${BuildKonfig.OPENAI_URL}/v1/responses",
        requestBody = HttpClient.RequestBody.create(createRequestForUrl(url))
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
      memoryCache.get(url.url)
    },
    intoMemory = { url, summary ->
      memoryCache.put(url.url, summary)
    },
  )

  public fun articleSummary(url: Url): Flow<Async<ArticleSummary>> {
    return summaryStore.collect(url, AsyncDataStore.LoadStrategy.CacheOnly)
  }

  private fun createRequestForUrl(url: Url): SummaryRequest {
    val prompt = """
      Summarize this article. Use no more than 700 symbols.
      Output format: Use only plain text (no markdown or html) in the output,
      do not include any links. Article url: ${url.url}
    """.trimIndent()

    return SummaryRequest(
      model = ApiConfig.MODEL,
      tools = listOf(SummaryRequest.Tool(ApiConfig.TOOL_WEB_SEARCH)),
      input = listOf(
        SummaryRequest.Input(
          role = ApiConfig.ROLE,
          content = listOf(
            SummaryRequest.Message(
              type = "input_text",
              text = prompt,
            )
          )
        )
      )
    )
  }
}
