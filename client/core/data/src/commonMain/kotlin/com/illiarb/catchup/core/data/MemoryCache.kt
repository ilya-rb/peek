package com.illiarb.catchup.core.data

public interface MemoryCache<K> {

  public fun put(key: K, value: Any)

  public fun <V> get(key: K): V?

  public fun delete(key: K)

  public fun contains(key: K): Boolean
}

public data class ConcurrentHashMapCache(val cache: DefaultConcurrentHashMapCache<String>) {

  public operator fun invoke(): MemoryCache<String> = cache
}