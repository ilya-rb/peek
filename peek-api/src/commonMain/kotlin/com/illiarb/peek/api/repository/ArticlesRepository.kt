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
    return Async.fromFlow {
      articlesDao.savedArticles().getOrThrow()
    }
  }

  public fun articleByUrl(url: Url): Flow<Async<Article>> {
    return Async.fromFlow {
      val article = articlesDao.articleByUrl(url).getOrElse { throw it }
      article ?: throw ArticleNotFoundException()
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
