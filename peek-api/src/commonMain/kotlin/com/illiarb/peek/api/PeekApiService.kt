package com.illiarb.peek.api

import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.ArticlesOfKind
import com.illiarb.peek.api.domain.NewsSourceKind
import com.illiarb.peek.api.repository.ArticlesRepository
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.types.Url
import kotlinx.coroutines.flow.Flow

public interface PeekApiService {

  public fun getAvailableSources(): List<NewsSourceKind>

  public fun collectLatestNewsFrom(kind: NewsSourceKind): Flow<Async<ArticlesOfKind>>

  public fun collectArticleByUrl(url: Url): Flow<Async<Article>>

  public fun collectSavedArticles(): Flow<Async<List<Article>>>

  public suspend fun saveArticle(article: Article): Result<Unit>
}

internal class DefaultPeekApiService(
  private val articlesRepository: ArticlesRepository,
  private val newsDataSources: Set<NewsDataSource>,
) : PeekApiService {

  override fun collectLatestNewsFrom(kind: NewsSourceKind): Flow<Async<ArticlesOfKind>> {
    return articlesRepository.articlesFrom(kind)
  }

  override fun getAvailableSources(): List<NewsSourceKind> {
    return newsDataSources.map { dataSource -> dataSource.kind }
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
