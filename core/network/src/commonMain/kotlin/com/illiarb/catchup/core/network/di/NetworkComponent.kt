package com.illiarb.catchup.core.network.di

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.network.DefaultHttpClient
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.core.network.TimeoutConfig
import com.illiarb.catchup.core.network.createKtorClient
import com.illiarb.catchup.core.network.plugins.debugDelayPlugin
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import me.tatarka.inject.annotations.Provides
import io.ktor.client.HttpClient as KtorClient

interface NetworkComponent {

  @[Provides]
  fun provideKtorClient(
    config: NetworkConfig,
    plugins: List<HttpClientPlugin<*, *>>,
  ): KtorClient = createKtorClient(config, plugins)

  @[Provides]
  fun providePlugins(): List<HttpClientPlugin<*, *>> {
    return listOf(
      debugDelayPlugin(),
    )
  }

  @[Provides]
  fun provideNetworkConfig(): NetworkConfig {
    return NetworkConfig(
      apiUrl = "http://10.0.2.2:8000", // https://developer.android.com/studio/run/emulator-networking
      timeouts = TimeoutConfig(
        connect = DEFAULT_TIMEOUT_SECONDS,
        read = DEFAULT_TIMEOUT_SECONDS,
        write = DEFAULT_TIMEOUT_SECONDS,
      ),
    )
  }

  @[Provides]
  fun provideHttpClient(
    appDispatchers: AppDispatchers,
    config: NetworkConfig,
    httpClient: KtorClient,
  ): HttpClient {
    return DefaultHttpClient(
      baseUrl = config.apiUrl,
      ktorHttpClient = httpClient,
      appDispatchers = appDispatchers,
    )
  }

  companion object {
    const val DEFAULT_TIMEOUT_SECONDS = 10L
  }
}


