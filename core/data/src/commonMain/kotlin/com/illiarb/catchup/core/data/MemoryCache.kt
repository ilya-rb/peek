package com.illiarb.catchup.core.data

import co.touchlab.stately.collections.ConcurrentMutableMap

public interface MemoryCache<K> {

  public fun put(key: K, value: Any)

  public fun <V> get(key: K): V?

  public fun delete(key: K)

  public fun contains(key: K): Boolean
}

public class HashMapCache<K> : MemoryCache<K> {

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