package com.illiarb.peek.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn

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

  public fun collect(params: Params, strategy: LoadStrategy): Flow<Async<Domain>> {
    return sharedFlows.getOrPut(params) {
      createFlowFor(params, strategy).shareIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(
          stopTimeoutMillis = 5000,
          replayExpirationMillis = 0, // Don't replay cached emissions
        ),
        replay = 0, // Don't cache emissions
      ).onCompletion {
        sharedFlows.remove(params)
      }
    }
  }

  public suspend fun updateLocal(params: Params, domain: Domain) {
    intoStorage.invoke(params, domain)
    intoMemory.invoke(params, domain)
  }

  public fun invalidateMemory(params: Params) {
    invalidateMemory.invoke(params)
  }

  private fun createFlowFor(params: Params, strategy: LoadStrategy): Flow<Async<Domain>> {
    return flow {
      val memCached = getFromMemory(params)
      if (memCached != null) {
        emit(Async.Content(memCached))
      }

      val storageCached = if (memCached != null) {
        null
      } else {
        emit(Async.Loading)
        getFromStorage(params)
      }

      if (storageCached != null) {
        emit(Async.Content(storageCached))
      }

      val fromNetwork = getFromNetwork(params, strategy, memCached, storageCached)

      fromNetwork?.onSuccess { content ->
        suspendRunCatching { intoStorage.invoke(params, content) }.onFailure { error ->
          Logger.e(TAG, error) { "Failed to update storage, params $params" }
        }

        suspendRunCatching { intoMemory.invoke(params, content) }.onFailure { error ->
          Logger.e(TAG, error) { "Failed to update memory cache, params $params" }
        }

        emit(Async.Content(content))
      }

      fromNetwork?.onFailure { error ->
        Logger.e(TAG, error) { "Failed to fetch from network, params $params" }

        if (memCached == null && storageCached == null) {
          emit(Async.Error(error))
        }
      }
    }.catch { error ->
      emit(Async.Error(error))
    }
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

  private suspend fun getFromNetwork(
    params: Params,
    strategy: LoadStrategy,
    fromMemory: Domain?,
    fromStorage: Domain?,
  ): Result<Domain>? {
    return when (strategy) {
      LoadStrategy.CacheFirst -> suspendRunCatching {
        networkFetcher.invoke(params)
      }

      LoadStrategy.CacheOnly -> {
        if (fromMemory == null && fromStorage == null) {
          suspendRunCatching {
            networkFetcher.invoke(params)
          }
        } else {
          null
        }
      }
    }
  }

  public sealed interface LoadStrategy {
    public data object CacheFirst : LoadStrategy
    public data object CacheOnly : LoadStrategy
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
