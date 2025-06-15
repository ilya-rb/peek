package com.illiarb.peek.core.network.di

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironment
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.HttpClientFactory
import com.illiarb.peek.core.network.plugins.debugDelayPlugin
import io.ktor.client.plugins.HttpClientPlugin
import me.tatarka.inject.annotations.Provides

public interface NetworkComponent {

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
  public fun provideHttpClientFactory(
    appDispatchers: AppDispatchers,
    plugins: List<HttpClientPlugin<*, *>>,
  ): HttpClient.Factory {
    return HttpClientFactory(plugins, appDispatchers)
  }
}


