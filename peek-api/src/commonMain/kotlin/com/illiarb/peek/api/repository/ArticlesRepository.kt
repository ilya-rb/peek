package com.illiarb.peek.api.repository

import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.db.dao.ArticlesDao
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.types.Url
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlin.jvm.JvmSuppressWildcards

@Inject
public class ArticlesRepository(
  private val memoryCache: ConcurrentHashMapCache,
  private val articlesDao: ArticlesDao,
  private val newsDataSources: Set<@JvmSuppressWildcards NewsDataSource>,
) {

  private val articlesStore = AsyncDataStore<NewsSourceKind, List<Article>>(
    networkFetcher = { kind ->
      dataSourceFor(kind).getArticles()
    },
    fromMemory = { kind ->
      memoryCache().get<List<Article>>(kind.name)?.takeIf { it.isNotEmpty() }
    },
    intoMemory = { kind, articles ->
      memoryCache().put(kind.name, articles)
    },
    fromStorage = { kind ->
      articlesDao.articlesOfKind(kind).getOrNull()?.takeIf { it.isNotEmpty() }
    },
    intoStorage = { _, articles ->
      articlesDao.saveArticles(articles)
    },
    invalidateMemory = { kind ->
      memoryCache().delete(kind.name)
    }
  )

  public fun articlesFrom(kind: NewsSourceKind): Flow<Async<List<Article>>> {
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
      articlesStore.invalidateMemory(article.kind)
    }
  }

  private fun dataSourceFor(kind: NewsSourceKind): NewsDataSource {
    return newsDataSources.first { it.kind == kind }
  }

  public class ArticleNotFoundException : Throwable()
}
