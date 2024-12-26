package com.illiarb.catchup.features.home.filters

import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.Tag

internal sealed interface ArticlesFilter {

  fun apply(items: Collection<Article>): Collection<Article>

  data object Saved : ArticlesFilter {
    override fun apply(items: Collection<Article>): Collection<Article> {
      return items.filter { it.saved }
    }
  }

  data class ByTag(val tags: Set<Tag>) : ArticlesFilter {
    override fun apply(items: Collection<Article>): Collection<Article> {
      return if (tags.isEmpty()) {
        items
      } else {
        items.filter { item ->
          item.tags.any { tag -> tag in tags }
        }
      }
    }
  }

  data class Composite(val filters: Set<ArticlesFilter>) : ArticlesFilter {

    override fun apply(items: Collection<Article>): Collection<Article> {
      return filters.fold(items) { articles, filter ->
        filter.apply(articles)
      }
    }
  }
}