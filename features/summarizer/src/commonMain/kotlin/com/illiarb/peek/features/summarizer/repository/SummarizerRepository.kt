package com.illiarb.peek.features.summarizer.repository

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.types.Currencies.USD
import com.illiarb.peek.core.types.Money
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.features.summarizer.BuildKonfig
import com.illiarb.peek.features.summarizer.db.ArticlesSummaryDao
import com.illiarb.peek.features.summarizer.di.InternalApi
import com.illiarb.peek.features.summarizer.domain.ArticleSummary
import com.illiarb.peek.features.summarizer.network.CompletionRequest
import com.illiarb.peek.features.summarizer.network.CompletionResponse
import com.illiarb.peek.features.summarizer.network.Message
import com.illiarb.peek.features.summarizer.network.OpenAiAPIConfig
import com.illiarb.peek.features.summarizer.network.RequestError
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
internal class SummarizerRepository(
  @InternalApi private val httpClient: HttpClient,
  private val summaryDao: ArticlesSummaryDao,
) {

  private val summaryStore = AsyncDataStore<Url, ArticleSummary>(
    networkFetcher = { url ->
      val request = HttpClient.RequestBody.create(createRequestFor(url))
      val response = httpClient
        .post("${BuildKonfig.OPENAI_URL}/v1/chat/completions", request)
        .getOrThrow()

      if (response.status.isSuccess()) {
        val completionResponse = response.body<CompletionResponse>()

        ArticleSummary(
          url = url,
          content = completionResponse.choices.first().message.content.trim(),
          price = calculateCost(completionResponse.usage),
          model = OpenAiAPIConfig.MODEL,
        )
      } else {
        error(response.body<RequestError>().message)
      }
    },
    fromStorage = { url ->
      summaryDao.summaryByUrl(url).getOrNull()
    },
    intoStorage = { _, summary ->
      summaryDao.saveSummary(summary)
    },
    fromMemory = { null },
    intoMemory = { _, _ -> },
  )

  fun articleSummary(url: Url): Flow<Async<ArticleSummary>> {
    return summaryStore.collect(url, AsyncDataStore.LoadStrategy.CacheOnly)
  }

  private fun createRequestFor(url: Url): CompletionRequest {
    val prompt = """
      Summarize this article.
      Output format: Use only plain text (no markdown or html) in the output.
      Article url: ${url.url}""".trimIndent()

    return CompletionRequest(
      model = OpenAiAPIConfig.MODEL,
      messages = listOf(Message(content = prompt, role = "user"))
    )
  }

  private fun calculateCost(usage: CompletionResponse.Usage): Money {
    val inputTokens = usage.promptTokens - usage.promptTokensDetails.cachedTokens
    val cachedTokens = usage.promptTokensDetails.cachedTokens
    val outputTokens = usage.completionTokens

    val inputCost = inputTokens * INPUT_PRICE_PER_TOKEN
    val cachedCost = cachedTokens * CACHED_INPUT_PRICE_PER_TOKEN
    val outputCost = outputTokens * OUTPUT_PRICE_PER_TOKEN

    return Money(inputCost + cachedCost + outputCost, USD)
  }

  private companion object {
    const val INPUT_PRICE_PER_TOKEN = 0.40 / 1_000_000
    const val CACHED_INPUT_PRICE_PER_TOKEN = 0.10 / 1_000_000
    const val OUTPUT_PRICE_PER_TOKEN = 1.60 / 1_000_000
  }
}
