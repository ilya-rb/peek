package com.illiarb.peek.core.network.di

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironment
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.network.DefaultHttpClient
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.HttpClientFactory
import com.illiarb.peek.core.network.NetworkConfig
import com.illiarb.peek.core.network.TimeoutConfig
import com.illiarb.peek.core.network.createKtorClient
import com.illiarb.peek.core.network.plugins.debugDelayPlugin
import io.ktor.client.plugins.HttpClientPlugin
import me.tatarka.inject.annotations.Provides
import io.ktor.client.HttpClient as KtorClient

public interface NetworkComponent {

  @Provides
  public fun provideKtorClient(
    config: NetworkConfig,
    plugins: List<HttpClientPlugin<*, *>>,
  ): KtorClient = createKtorClient(config, plugins)

  @Provides
  public fun providePlugins(
    appConfiguration: AppConfiguration,
    environment: AppEnvironment,
  ): List<HttpClientPlugin<*, *>> {
    return when (environment) {
      AppEnvironment.PROD -> emptyList()
      AppEnvironment.DEV -> {
        listOf(
          debugDelayPlugin(appConfiguration)
        )
      }
    }
  }

  @Provides
  public fun provideNetworkConfig(): NetworkConfig {
    return NetworkConfig(
      apiUrl = "http://10.0.2.2:8000", // https://developer.android.com/studio/run/emulator-networking
      timeouts = TimeoutConfig.default(),
    )
  }

  @Provides
  public fun provideHttpClient(
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

  @Provides
  public fun provideHttpClientFactory(
    appDispatchers: AppDispatchers,
    plugins: List<HttpClientPlugin<*, *>>,
  ): HttpClient.Factory {
    return HttpClientFactory(plugins, appDispatchers)
  }
}


