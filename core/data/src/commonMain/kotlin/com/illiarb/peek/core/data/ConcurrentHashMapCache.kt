package com.illiarb.peek.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap

public class ConcurrentHashMapCache<K> : MemoryCache<K> {

  private val cache = ConcurrentMutableMap<K, Any>()

  override fun put(key: K, value: Any) {
    cache[key] = value
  }

  override fun <V> get(key: K): V? {
    @Suppress("UNCHECKED_CAST")
    return cache[key] as? V
  }

  override fun <V : Any> getOrCreate(key: K, creator: () -> V): V {
    @Suppress("UNCHECKED_CAST")
    return cache.getOrPut(key, creator) as V
  }

  override fun delete(key: K) {
    cache.remove(key)
  }

  override fun contains(key: K): Boolean {
    return cache.contains(key)
  }
}
