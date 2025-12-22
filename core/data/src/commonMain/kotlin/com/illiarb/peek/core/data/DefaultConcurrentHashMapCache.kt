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

  override fun <V : Any> getOrCreate(key: K, creator: () -> V): V {
    @Suppress("UNCHECKED_CAST")
    val cached = cache[key] as? V
    return cached ?: creator().also { put(key, it) }
  }

  override fun delete(key: K) {
    cache.remove(key)
  }

  override fun contains(key: K): Boolean {
    return cache.contains(key)
  }
}
