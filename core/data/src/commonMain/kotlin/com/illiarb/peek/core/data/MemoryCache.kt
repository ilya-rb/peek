package com.illiarb.peek.core.data

public interface MemoryCache<K> {

  public fun put(key: K, value: Any)

  public fun <V> get(key: K): V?

  public fun <V : Any> getOrCreate(key: K, creator: () -> V): V

  public fun delete(key: K)

  public fun contains(key: K): Boolean
}
