package com.illiarb.peek.core.network.plugins

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.AppEnvironmentState
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

internal fun debugDelayPlugin(appConfiguration: AppConfiguration) =
  createClientPlugin(
    name = "DebugDelayPlugin",
    createConfiguration = {},
  ) {
    onRequest { _, _ ->
      val delayEnabled = if (AppEnvironmentState.isDev()) {
        appConfiguration.debugConfig().first().networkDelayEnabled
      } else {
        false
      }
      if (delayEnabled) {
        delay(3000L)
      }
    }
  }
