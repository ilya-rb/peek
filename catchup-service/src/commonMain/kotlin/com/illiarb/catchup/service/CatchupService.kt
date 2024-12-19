package com.illiarb.catchup.service

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.repository.ArticlesRepository
import com.illiarb.catchup.service.repository.NewsSourcesRepository
import kotlinx.coroutines.flow.Flow

public interface CatchupService {

  public fun collectLatestNewsFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>>

  public fun collectAvailableSources(): Flow<Async<Set<NewsSource>>>

  public fun collectArticleById(id: String): Flow<Async<Article>>

  public suspend fun saveArticle(article: Article): Result<Unit>
}

internal class DefaultCatchupService(
  private val articlesRepository: ArticlesRepository,
  private val newsSourcesRepository: NewsSourcesRepository,
) : CatchupService {

  override fun collectLatestNewsFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>> {
    return articlesRepository.articlesFrom(kind)
  }

  override fun collectAvailableSources(): Flow<Async<Set<NewsSource>>> {
    return newsSourcesRepository.allSources()
  }

  override fun collectArticleById(id: String): Flow<Async<Article>> {
    return articlesRepository.articleById(id)
  }

  override suspend fun saveArticle(article: Article): Result<Unit> {
    return articlesRepository.saveArticle(article)
  }
}