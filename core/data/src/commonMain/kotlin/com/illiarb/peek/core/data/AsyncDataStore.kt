package com.illiarb.peek.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

public class AsyncDataStore<Params, Domain>(
  private val networkFetcher: suspend (Params) -> Domain,
  private val fromStorage: suspend (Params) -> Domain?,
  private val intoStorage: suspend (Params, Domain) -> Unit,
  private val fromMemory: (Params) -> Domain?,
  private val intoMemory: (Params, Domain) -> Unit,
  private val invalidateMemory: (Params) -> Unit = {},
) {

  private val sharedFlows = ConcurrentMutableMap<Params, Flow<Async<Domain>>>()
  private val coroutineScope: CoroutineScope
    get() = AsyncDataStoreGlobal.coroutineScope

  public fun collect(params: Params, strategy: LoadStrategy<Params>): Flow<Async<Domain>> {
    return sharedFlows.getOrPut(params) {
      createFlowFor(params, strategy).shareIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(
          stopTimeoutMillis = 5000,
          replayExpirationMillis = 0,
        ),
        replay = 0,
      ).onCompletion {
        sharedFlows.remove(params)
      }
    }
  }

  public suspend fun updateLocal(params: Params, domain: Domain) {
    intoStorage.invoke(params, domain)
    intoMemory.invoke(params, domain)
  }

  public suspend fun invalidateMemory(params: Params) {
    invalidateMemory.invoke(params)

    val storageCached = fromStorage.invoke(params)
    if (storageCached != null) {
      intoMemory.invoke(params, storageCached)
    }
  }

  private fun createFlowFor(params: Params, strategy: LoadStrategy<Params>): Flow<Async<Domain>> {
    return flow {
      val networkRefreshRequired = when (strategy) {
        is LoadStrategy.ForceReload, LoadStrategy.CacheFirst -> true
        is LoadStrategy.TimeBased<Params> -> strategy.cacheExpired(params)
        else -> false
      }

      val memCached = if (strategy is LoadStrategy.ForceReload) {
        null
      } else {
        getFromMemory(params)
      }
      if (memCached != null) {
        emit(Async.Content(memCached, networkRefreshRequired))
      }

      val storageCached = if (memCached != null || strategy is LoadStrategy.ForceReload) {
        null
      } else {
        getFromStorage(params)
      }
      if (storageCached != null) {
        emit(Async.Content(storageCached, networkRefreshRequired))
      }

      val noCache = memCached == null && storageCached == null
      if (noCache) {
        emit(Async.Loading)
      }

      val fromNetwork = if (networkRefreshRequired || noCache) {
        getFromNetwork(params, strategy)
      } else {
        null
      }

      fromNetwork?.fold(
        onSuccess = { content ->
          onNetworkSuccess(params, content)
        },
        onFailure = { error ->
          onNetworkFailure(error, params, memCached, storageCached)
        },
      )
    }.catch { error ->
      emit(Async.Error(error))
    }
  }

  private suspend fun FlowCollector<Async<Domain>>.onNetworkSuccess(
    params: Params,
    content: Domain,
  ) {
    suspendRunCatching { intoStorage.invoke(params, content) }.onFailure { error ->
      Logger.e(TAG, error) { "Failed to update storage, params $params" }
    }

    suspendRunCatching { intoMemory.invoke(params, content) }.onFailure { error ->
      Logger.e(TAG, error) { "Failed to update memory cache, params $params" }
    }

    emit(Async.Content(content, contentRefreshing = false))
  }

  private suspend fun FlowCollector<Async<Domain>>.onNetworkFailure(
    error: Throwable,
    params: Params,
    memCached: Domain?,
    storageCached: Domain?,
  ) {
    Logger.e(TAG, error) { "Failed to fetch from network, params $params" }

    emit(
      when {
        memCached != null -> Async.Content(
          memCached,
          contentRefreshing = false,
          suppressedError = error
        )

        storageCached != null -> Async.Content(
          storageCached,
          contentRefreshing = false,
          suppressedError = error
        )

        else -> Async.Error(error)
      }
    )
  }

  private suspend fun getFromMemory(params: Params): Domain? {
    return suspendRunCatching { fromMemory.invoke(params) }
      .onFailure { error ->
        Logger.e(TAG, error) { "Failed to read from memory cache, skipping.." }
      }
      .getOrNull()
  }

  private suspend fun getFromStorage(params: Params): Domain? {
    return suspendRunCatching { fromStorage.invoke(params) }
      .onFailure { error ->
        Logger.e(TAG, error) { "Failed to read from storage, skipping.." }
      }
      .onSuccess { fromStorage ->
        if (fromStorage != null) {
          suspendRunCatching { intoMemory.invoke(params, fromStorage) }.onFailure { error ->
            Logger.e(TAG, error) { "Failed to warmup memory cache" }
          }
        }
      }
      .getOrNull()
  }

  private suspend fun LoadStrategy.TimeBased<Params>.cacheExpired(params: Params): Boolean {
    if (invalidate) {
      return true
    }

    val current = invalidator.getCacheTimestamp(params)
      .onFailure { error ->
        Logger.e(TAG, error) { "Failed to read cached timestamp for $params" }
      }
      .getOrNull()

    return current == null || (Clock.System.now() - current) > duration
  }

  private suspend fun getFromNetwork(
    params: Params,
    strategy: LoadStrategy<Params>
  ): Result<Domain> {
    return suspendRunCatching { networkFetcher.invoke(params) }.onSuccess {
      if (strategy is LoadStrategy.TimeBased<Params>) {
        strategy.invalidator.setCacheTimestamp(params, Clock.System.now()).onFailure { error ->
          Logger.e(TAG, error) { "Failed to save cached timestamp for $params" }
        }
      }
    }
  }

  public sealed interface LoadStrategy<out P> {

    public data object CacheFirst : LoadStrategy<Nothing>

    public data object CacheOnly : LoadStrategy<Nothing>

    public data object ForceReload : LoadStrategy<Nothing>

    public data class TimeBased<P>(
      val duration: Duration,
      val invalidator: CacheInvalidator<P>,
      internal val invalidate: Boolean = false,
    ) : LoadStrategy<P> {

      public interface CacheInvalidator<P> {
        public suspend fun getCacheTimestamp(params: P): Result<Instant?>
        public suspend fun setCacheTimestamp(params: P, time: Instant): Result<Unit>
      }
    }
  }

  internal companion object {
    const val TAG: String = "AsyncDataStore"
  }
}

internal object AsyncDataStoreGlobal {

  private val job = SupervisorJob()

  val coroutineScope = CoroutineScope(job)

  fun cancelPendingRequests() {
    job.cancelChildren()
  }
}
