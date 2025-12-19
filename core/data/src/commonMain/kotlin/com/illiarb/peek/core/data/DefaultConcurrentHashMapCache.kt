package com.illiarb.peek.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap

public class DefaultConcurrentHashMapCache<K> : MemoryCache<K> {

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
