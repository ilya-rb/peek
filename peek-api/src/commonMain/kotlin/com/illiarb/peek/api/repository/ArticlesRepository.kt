package com.illiarb.peek.api.repository

import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.db.dao.ArticlesDao
import com.illiarb.peek.api.di.InternalApi
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.ArticlesOfKind
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.error.ArticleNotFoundException
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.AsyncDataStore
import com.illiarb.peek.core.data.AsyncDataStore.LoadStrategy.ForceReload
import com.illiarb.peek.core.data.AsyncDataStore.LoadStrategy.TimeBased
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.MemoryCache
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.core.logging.Logger
import com.illiarb.peek.core.types.Url
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmSuppressWildcards
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
internal class ArticlesRepository(
  @InternalApi private val memoryCache: MemoryCache<String>,
  @InternalApi private val storage: KeyValueStorage,
  private val articlesDao: ArticlesDao,
  private val newsDataSources: Set<@JvmSuppressWildcards NewsDataSource>,
) {

  private val articlesLoadingStrategy = TimeBased(
    duration = 3.hours,
    invalidator = object : TimeBased.CacheInvalidator<NewsSourceKind> {
      override suspend fun getCacheTimestamp(params: NewsSourceKind): Result<Instant?> {
        return storage.get(params.storageKey(), Long.serializer()).map { stamp ->
          stamp?.let { Instant.fromEpochMilliseconds(it) }
        }
      }

      override suspend fun setCacheTimestamp(params: NewsSourceKind, time: Instant): Result<Unit> {
        return storage.put(params.storageKey(), time.toEpochMilliseconds(), Long.serializer())
      }
    }
  )

  private val articlesStore = AsyncDataStore<NewsSourceKind, List<Article>>(
    networkFetcher = { kind ->
      val newArticles = kind.dataSource().getArticles()
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
    },
  )

  fun articlesFrom(
    kind: NewsSourceKind,
    strategy: AsyncDataStore.LoadStrategy<NewsSourceKind>? = null,
  ): Flow<Async<ArticlesOfKind>> {
    return articlesStore.collect(kind, articlesLoadingStrategy.overrideIfNeeded(strategy))
      .mapContent { articles ->
        articles.sortedByDescending { it.date }.toArticlesOfKind(kind)
      }
  }

  fun savedArticles(): Flow<Async<List<Article>>> {
    return articlesDao.savedArticles()
      .map { articles ->
        articles.sortedByDescending {
          it.date
        }
      }
      .map { Async.Content(it, contentRefreshing = false) as Async<List<Article>> }
      .catch { emit(Async.Error(it)) }
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

  suspend fun deleteArticlesOlderThan(duration: Duration): Result<Unit> {
    return articlesDao.deleteArticlesOlderThan(duration)
  }

  private suspend fun List<Article>.toArticlesOfKind(kind: NewsSourceKind): ArticlesOfKind {
    val timestamp = requireNotNull(
      articlesLoadingStrategy.invalidator.getCacheTimestamp(kind).getOrThrow()
    )

    return ArticlesOfKind(
      kind = kind,
      articles = this,
      lastUpdated = timestamp,
    )
  }

  private fun NewsSourceKind.dataSource(): NewsDataSource {
    return newsDataSources.first { it.kind == this }
  }

  private fun NewsSourceKind.storageKey(): String {
    return "${KEY_ARTICLES_LAST_FETCHED_PREFIX}_${name}"
  }

  private fun TimeBased<NewsSourceKind>.overrideIfNeeded(
    requested: AsyncDataStore.LoadStrategy<NewsSourceKind>?,
  ): AsyncDataStore.LoadStrategy<NewsSourceKind> {
    return if (requested is ForceReload) {
      copy(invalidate = true)
    } else {
      requested ?: this
    }
  }

  companion object {
    const val KEY_ARTICLES_LAST_FETCHED_PREFIX = "KEY_ARTICLES_LAST_FETCHED_PREFIX"
  }
}
