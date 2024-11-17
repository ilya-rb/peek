package com.illiarb.catchup.service.di

import com.illiarb.catchup.core.arch.di.AppScope
import com.illiarb.catchup.core.data.HashMapCache
import com.illiarb.catchup.core.data.MemoryCache
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.service.network.CatchupService
import com.illiarb.catchup.service.network.CatchupServiceRepository
import me.tatarka.inject.annotations.Provides
import kotlin.jvm.JvmSuppressWildcards

@AppScope
interface CatchupServiceComponent {

  @[Provides AppScope]
  fun provideMemoryCache(): Cache = Cache(HashMapCache())

  @Provides
  fun provideCatchupService(
    httpClient: HttpClient,
    networkConfig: NetworkConfig,
    cache: Cache,
  ): CatchupService = CatchupServiceRepository(httpClient, networkConfig, cache.value)

  data class Cache(val value: MemoryCache<String>)
}
