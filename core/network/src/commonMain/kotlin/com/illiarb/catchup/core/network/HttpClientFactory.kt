package com.illiarb.catchup.core.network

import com.illiarb.catchup.core.coroutines.AppDispatchers
import io.ktor.client.plugins.HttpClientPlugin

internal class HttpClientFactory(
  private val plugins: List<HttpClientPlugin<*, *>>,
  private val appDispatchers: AppDispatchers,
) : HttpClient.Factory {

  override fun create(
    baseUrl: String,
    plugins: List<HttpClientPlugin<*, *>>,
  ): HttpClient {
    return DefaultHttpClient(
      appDispatchers = appDispatchers,
      baseUrl = baseUrl,
      ktorHttpClient = createKtorClient(
        plugins = this.plugins + plugins,
        config = NetworkConfig(
          apiUrl = baseUrl,
          timeouts = TimeoutConfig.default(),
        )
      ),
    )
  }
}