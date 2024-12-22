package com.illiarb.catchup.core.network.di

import com.illiarb.catchup.core.appinfo.AppConfiguration
import com.illiarb.catchup.core.appinfo.AppEnvironment
import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.network.DefaultHttpClient
import com.illiarb.catchup.core.network.HttpClient
import com.illiarb.catchup.core.network.NetworkConfig
import com.illiarb.catchup.core.network.TimeoutConfig
import com.illiarb.catchup.core.network.createKtorClient
import com.illiarb.catchup.core.network.plugins.debugDelayPlugin
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
      timeouts = TimeoutConfig(
        connect = DEFAULT_TIMEOUT_SECONDS,
        read = DEFAULT_TIMEOUT_SECONDS,
        write = DEFAULT_TIMEOUT_SECONDS,
      ),
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

  public companion object {
    public const val DEFAULT_TIMEOUT_SECONDS: Long = 10L
  }
}


