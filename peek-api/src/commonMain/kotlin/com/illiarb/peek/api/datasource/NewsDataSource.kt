package com.illiarb.peek.api.datasource

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind

public interface NewsDataSource {

  public val kind: NewsSourceKind

  public suspend fun getArticles(): List<Article>
}
