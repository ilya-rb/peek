package com.illiarb.peek.api

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.types.Url
import com.illiarb.peek.api.repository.ArticlesRepository
import com.illiarb.peek.api.repository.NewsSourcesRepository
import com.illiarb.peek.core.data.Async
import kotlinx.coroutines.flow.Flow

public interface PeekApiService {

  public fun collectLatestNewsFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>>

  public fun collectAvailableSources(): Flow<Async<Set<NewsSource>>>

  public fun collectArticleByUrl(url: Url): Flow<Async<Article>>

  public fun collectSavedArticles(): Flow<Async<List<Article>>>

  public suspend fun saveArticle(article: Article): Result<Unit>
}

internal class DefaultPeekApiService(
  private val articlesRepository: ArticlesRepository,
  private val newsSourcesRepository: NewsSourcesRepository,
) : PeekApiService {

  override fun collectLatestNewsFrom(kind: NewsSource.Kind): Flow<Async<List<Article>>> {
    return articlesRepository.articlesFrom(kind)
  }

  override fun collectAvailableSources(): Flow<Async<Set<NewsSource>>> {
    return newsSourcesRepository.allSources()
  }

  override fun collectArticleByUrl(url: Url): Flow<Async<Article>> {
    return articlesRepository.articleByUrl(url)
  }

  override fun collectSavedArticles(): Flow<Async<List<Article>>> {
    return articlesRepository.savedArticles()
  }

  override suspend fun saveArticle(article: Article): Result<Unit> {
    return articlesRepository.saveArticle(article)
  }
}