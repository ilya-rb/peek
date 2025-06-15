package com.illiarb.peek.api.repository

import com.illiarb.peek.api.db.dao.ArticlesDao
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.api.network.dto.ArticlesDto
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.network.HttpClient
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import me.tatarka.inject.annotations.Inject

@Inject
public class ArticlesRepository(
  private val httpClient: HttpClient,
  private val memoryCache: ConcurrentHashMapCache,
  private val articlesDao: ArticlesDao,
) {

  private val articlesStore = AsyncDataStore<NewsSource.Kind, List<Article>>(
    networkFetcher = { kind ->
      httpClient.get(path = "news", parameters = mapOf("source" to kind.key))
        .map {
          val response = it.body<ArticlesDto>()
          val savedIds = articlesDao.savedArticlesUrls().getOrNull().orEmpty()
          response.articles.map { dto -> dto.asArticle(savedIds) }
        }
        .getOrThrow()
    },
    fromMemory = { kind ->
      memoryCache().get(kind.key)
    },
    intoMemory = { kind, articles ->
      memoryCache().put(kind.key, articles)
    },
    fromStorage = { kind ->
      articlesDao.articlesBySource(kind).getOrNull()
    },
    intoStorage = { _, articles ->
      articlesDao.saveArticles(articles)
    },
    invalidateMemory = { kind ->
      memoryCache().delete(kind.key)
    }
  )

  public fun articlesFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>> {
    return articlesStore.collect(kind, AsyncDataStore.LoadStrategy.CacheFirst)
  }

  public fun savedArticles(): Flow<Async<List<Article>>> {
    return flow {
      articlesDao.savedArticles().fold(
        onSuccess = {
          emit(Async.Content(it))
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

  public fun articleByUrl(url: Url): Flow<Async<Article>> {
    return flow {
      articlesDao.articleByUrl(url).fold(
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

  public suspend fun saveArticle(article: Article): Result<Unit> {
    return articlesDao.saveArticle(article).onSuccess {
      articlesStore.invalidateMemory(article.source)
    }
  }

  public class ArticleNotFoundException : Throwable()
}
