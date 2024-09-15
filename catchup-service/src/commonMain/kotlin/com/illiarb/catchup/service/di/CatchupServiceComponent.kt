package com.illiarb.catchup.service.di

import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.service.network.CatchupService
import com.illiarb.catchup.service.network.CatchupServiceRepository
import me.tatarka.inject.annotations.Provides

interface CatchupServiceComponent {

  @Provides
  fun provideCatchupService(
    httpClient: HttpClient,
    networkConfig: NetworkConfig
  ): CatchupService = CatchupServiceRepository(httpClient, networkConfig)
}
