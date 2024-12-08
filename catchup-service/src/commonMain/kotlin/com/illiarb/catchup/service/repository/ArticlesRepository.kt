package com.illiarb.catchup.service.repository

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.core.data.AsyncDataStore
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.service.db.dao.ArticlesDao
import com.illiarb.catchup.service.di.CatchupServiceComponent.Cache
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.network.dto.ArticleDto
import com.illiarb.catchup.service.network.dto.ArticlesDto
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import me.tatarka.inject.annotations.Inject

@Inject
class ArticlesRepository(
  private val httpClient: HttpClient,
  private val memoryCache: Cache,
  private val articlesDao: ArticlesDao,
) {

  private val articlesStore = AsyncDataStore<NewsSource.Kind, List<Article>>(
    networkFetcher = { kind ->
      httpClient.get(path = "news", parameters = mapOf("source" to kind.key))
        .map {
          val response = it.body<ArticlesDto>()
          response.articles.map(ArticleDto::asArticle)
        }
        .getOrThrow()
    },
    fromMemory = { kind ->
      memoryCache.value.get(kind.key)
    },
    intoMemory = { kind, articles ->
      memoryCache.value.put(kind.key, articles)
    },
    fromStorage = { kind ->
      articlesDao.articlesBySource(kind).getOrNull()
    },
    intoStorage = { kind, articles ->
      articlesDao.deleteAndInsert(kind, articles)
    },
  )

  fun articlesFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>> {
    return articlesStore.collect(kind, AsyncDataStore.LoadStrategy.CacheFirst)
  }

  fun articleById(id: String): Flow<Async<Article>> {
    return flow {
      articlesDao.articleById(id).fold(
        onSuccess = {
          if (it != null) {
            emit(Async.Content(it))
          } else {
            emit(Async.Error(ArticleNotFoundException()))
          }
        },
        onFailure = { error ->
          emit(Async.Error(error))
        }
      )
    }.onStart {
      emit(Async.Loading)
    }.catch { error ->
      emit(Async.Error(error))
    }
  }

  class ArticleNotFoundException : Throwable()
}
