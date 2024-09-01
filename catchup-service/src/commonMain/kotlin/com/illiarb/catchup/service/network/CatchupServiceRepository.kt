package com.illiarb.catchup.service.network

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.asAsync
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.network.dto.ArticleDto
import com.illiarb.catchup.service.network.dto.ArticlesResponse
import com.illiarb.catchup.service.network.dto.NewsSourcesResponse
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import me.tatarka.inject.annotations.Inject

interface CatchupService {

  fun collectLatestNewsFrom(source: NewsSource): Flow<Async<List<Article>>>

  fun collectAvailableSources(): Flow<Async<Set<NewsSource>>>
}

@Inject
internal class CatchupServiceRepository(
  private val httpClient: HttpClient,
) : CatchupService {

  override fun collectLatestNewsFrom(source: NewsSource): Flow<Async<List<Article>>> =
    flow {
      val articles = httpClient.get(
        path = "news",
        parameters = mapOf("source" to source.id.key)
      ).map {
        val response = it.body<ArticlesResponse>()
        response.articles.map(ArticleDto::asArticle)
      }
      emit(articles.asAsync())
    }.onStart {
      emit(Async.Loading)
    }.catch {
      emit(Async.Error(it))
    }

  override fun collectAvailableSources(): Flow<Async<Set<NewsSource>>> =
    flow {
      val sources = httpClient.get(path = "supported_sources").map {
        val response = it.body<NewsSourcesResponse>()
        response.asNewsSourcesSet()
      }
      emit(sources.asAsync())
    }.onStart {
      emit(Async.Loading)
    }.catch {
      emit(Async.Error(it))
    }
}
