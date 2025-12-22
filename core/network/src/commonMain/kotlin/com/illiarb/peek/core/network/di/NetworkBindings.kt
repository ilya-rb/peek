package com.illiarb.peek.core.network.di

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironmentState
import com.illiarb.peek.core.network.HttpClient
import com.illiarb.peek.core.network.HttpClientFactory
import com.illiarb.peek.core.network.plugins.debugDelayPlugin
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import io.ktor.client.plugins.HttpClientPlugin

@BindingContainer
public abstract class NetworkBindings {

  @Binds
  internal abstract val HttpClientFactory.bind: HttpClient.Factory

  internal companion object {

    @Provides
    fun providePlugins(appConfiguration: AppConfiguration): List<HttpClientPlugin<*, *>> {
      return if (AppEnvironmentState.isDev()) {
        listOf(
          debugDelayPlugin(appConfiguration),
        )
      } else {
        emptyList()
      }
    }
  }
}


