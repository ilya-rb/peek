package com.illiarb.peek.core.network

import com.illiarb.peek.core.coroutines.AppDispatchers
import dev.zacsweers.metro.Inject
import io.ktor.client.plugins.HttpClientPlugin

@Inject
internal class HttpClientFactory(
  private val plugins: List<HttpClientPlugin<*, *>>,
  private val appDispatchers: AppDispatchers,
) : HttpClient.Factory {

  override fun create(
    config: NetworkConfig,
    plugins: List<HttpClientPlugin<*, *>>,
  ): HttpClient {
    return DefaultHttpClient(
      appDispatchers = appDispatchers,
      ktorHttpClient = createKtorClient(
        plugins = this.plugins + plugins,
        config = config,
      ),
    )
  }
}
