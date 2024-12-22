package com.illiarb.catchup.core.network.plugins

import com.illiarb.catchup.core.appinfo.AppConfiguration
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

internal fun debugDelayPlugin(appConfiguration: AppConfiguration) =
  createClientPlugin(
    name = "DebugDelayPlugin",
    createConfiguration = {},
  ) {
    onRequest { _, _ ->
      val delayEnabled = appConfiguration.debugConfig().first().networkDelayEnabled
      if (delayEnabled) {
        delay(3000L)
      }
    }
  }
