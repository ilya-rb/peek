package com.illiarb.peek.api.datasource

import com.illiarb.peek.api.domain.Article
import com.illiarb.peek.api.domain.NewsSourceKind

internal interface NewsDataSource {

  val kind: NewsSourceKind

  suspend fun getArticles(): List<Article>
}
