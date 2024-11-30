package com.illiarb.catchup.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap

interface MemoryCache<K> {

  fun put(key: K, value: Any)

  fun <V> get(key: K): V?

  fun delete(key: K)

  fun contains(key: K): Boolean
}

class HashMapCache<K> : MemoryCache<K> {

  private val cache = ConcurrentMutableMap<K, Any>()

  override fun put(key: K, value: Any) {
    cache[key] = value
  }

  override fun <V> get(key: K): V? {
    @Suppress("UNCHECKED_CAST")
    return cache[key] as? V
  }

  override fun delete(key: K) {
    cache.remove(key)
  }

  override fun contains(key: K): Boolean {
    return cache.contains(key)
  }
}