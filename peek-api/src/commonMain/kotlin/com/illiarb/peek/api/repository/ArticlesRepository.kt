package com.illiarb.peek.api.repository

import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.db.dao.ArticlesDao
import com.illiarb.peek.api.di.InternalApi
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.error.ArticleNotFoundException
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.MemoryCache
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.core.logging.Logger
import com.illiarb.peek.core.types.Url
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

@Inject
@SingleIn(AppScope::class)
internal class ArticlesRepository(
  @InternalApi private val memoryCache: MemoryCache<String>,
  private val articlesDao: ArticlesDao,
  private val newsDataSources: Set<@JvmSuppressWildcards NewsDataSource>,
) {

  private val articlesStore = AsyncDataStore<NewsSourceKind, List<Article>>(
    networkFetcher = { kind ->
      val newArticles = dataSourceFor(kind).getArticles()
      val cached = articlesDao.savedArticlesUrls().getOrElse { error ->
        Logger.e(throwable = error) { "Error reading cached articles" }
        emptyList()
      }
      newArticles.map {
        it.copy(saved = it.url in cached)
      }
    },
    fromMemory = { kind ->
      memoryCache.get<List<Article>>(kind.name)?.takeIf { it.isNotEmpty() }
    },
    intoMemory = { kind, articles ->
      memoryCache.put(kind.name, articles)
    },
    fromStorage = { kind ->
      articlesDao.articlesOfKind(kind).getOrNull()?.takeIf { it.isNotEmpty() }
    },
    intoStorage = { _, articles ->
      articlesDao.saveArticles(articles)
    },
    invalidateMemory = { kind ->
      memoryCache.delete(kind.name)
    }
  )

  fun articlesFrom(kind: NewsSourceKind): Flow<Async<List<Article>>> {
    return articlesStore.collect(kind, AsyncDataStore.LoadStrategy.CacheOnly)
      .mapContent { articles ->
        articles.sortedByDescending {
          it.date
        }
      }
  }

  fun savedArticles(): Flow<Async<List<Article>>> {
    return Async.fromFlow {
      articlesDao.savedArticles().getOrThrow().sortedByDescending {
        it.date
      }
    }
  }

  fun articleByUrl(url: Url): Flow<Async<Article>> {
    return Async.fromFlow {
      val article = articlesDao.articleByUrl(url).getOrElse { throw it }
      article ?: throw ArticleNotFoundException()
    }
  }

  suspend fun saveArticle(article: Article): Result<Unit> {
    return articlesDao.saveArticle(article).onSuccess {
      articlesStore.invalidateMemory(article.kind)
    }
  }

  private fun dataSourceFor(kind: NewsSourceKind): NewsDataSource {
    return newsDataSources.first { it.kind == kind }
  }
}
