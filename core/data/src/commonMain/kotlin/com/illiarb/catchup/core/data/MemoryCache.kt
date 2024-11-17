package com.illiarb.catchup.core.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MemoryCache<K> {

  suspend fun put(key: K, value: Any)

  suspend fun <V> get(key: K): V?

  suspend fun delete(key: K)

  suspend fun contains(key: K): Boolean
}

class HashMapCache<K> : MemoryCache<K> {

  private val cache = mutableMapOf<K, Any>()
  private val cacheLock = Mutex()

  override suspend fun put(key: K, value: Any) {
    cacheLock.withLock {
      cache[key] = value
    }
  }

  override suspend fun <V> get(key: K): V? {
    return cacheLock.withLock {
      @Suppress("UNCHECKED_CAST")
      cache[key] as? V
    }
  }

  override suspend fun delete(key: K) {
    cacheLock.withLock {
      cache.remove(key)
    }
  }

  override suspend fun contains(key: K): Boolean {
    return cacheLock.withLock {
      cache.contains(key)
    }
  }
}