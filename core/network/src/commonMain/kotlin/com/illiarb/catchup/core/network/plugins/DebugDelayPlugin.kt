package com.illiarb.catchup.core.network.plugins

import com.illiarb.catchup.core.appinfo.AppDebugToggles
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.delay

fun debugDelayPlugin() = createClientPlugin(
  name = "DebugDelayPlugin",
  createConfiguration = {},
) {
  onRequest { _, _ ->
    if (AppDebugToggles.networkDelayEnabled) {
      delay(3000L)
    }
  }
}
