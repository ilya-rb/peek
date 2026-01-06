package com.illiarb.peek.api.repository

import com.illiarb.peek.api.datasource.NewsDataSource
import com.illiarb.peek.api.di.InternalApi
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.logging.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmSuppressWildcards

@Inject
@SingleIn(AppScope::class)
internal class NewsSourcesRepository(
  @InternalApi private val storage: KeyValueStorage,
  private val newsDataSources: Set<@JvmSuppressWildcards NewsDataSource>,
) {

  fun collectAvailableSources(): Flow<Async<List<NewsSource>>> {
    return flow {
      val sources = storage.get(KEY_SERVICES_META, NewsSources.serializer()).getOrThrow()
      if (sources == null) {
        warmupStorage()
      }
      emitAll(storage.observe(KEY_SERVICES_META, NewsSources.serializer()))
    }.map {
      Async.Content(it.sources, contentRefreshing = false)
    }
  }

  suspend fun updateAvailableNewsSources(sources: List<NewsSource>) {
    storage.put(KEY_SERVICES_META, NewsSources(sources), NewsSources.serializer())
      .onFailure { error ->
        Logger.e(throwable = error)
      }
  }

  private suspend fun warmupStorage() {
    val initial = getInitialSourcesValue()
    storage.put(KEY_SERVICES_META, initial, NewsSources.serializer())
  }

  private fun getInitialSourcesValue(): NewsSources {
    return NewsSources(
      newsDataSources.mapIndexed { index, source ->
        NewsSource(source.kind, index)
      }
    )
  }

  @Serializable
  data class NewsSources(val sources: List<NewsSource>)

  companion object {
    const val KEY_SERVICES_META = "KEY_SERVICES_META"
  }
}
