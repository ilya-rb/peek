package com.illiarb.catchup.service

import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.NewsSource
import com.illiarb.catchup.service.repository.ArticlesRepository
import com.illiarb.catchup.service.repository.NewsSourcesRepository
import kotlinx.coroutines.flow.Flow

interface CatchupService {

  fun collectLatestNewsFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>>

  fun collectAvailableSources(): Flow<Async<Set<NewsSource>>>

  fun collectArticleById(id: String): Flow<Async<Article>>
}

class DefaultCatchupService(
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
}