package com.illiarb.catchup.features.home

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

    fun withSaved(filter: Saved): Composite {
      val newFilters = filters.toMutableSet()
      if (!newFilters.add(filter)) {
        newFilters.remove(filter)
      }
      return copy(filters = newFilters)
    }

    fun withTags(byTag: ByTag): Composite {
      return copy(
        filters = filters.map { item ->
          if (item is ByTag) {
            byTag
          } else {
            item
          }
        }.toSet()
      )
    }
  }
}