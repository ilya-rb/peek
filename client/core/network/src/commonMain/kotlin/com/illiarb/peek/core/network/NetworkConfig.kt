package com.illiarb.peek.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin

public data class NetworkConfig(
  val apiUrl: String,
  val timeouts: TimeoutConfig,
)

public data class TimeoutConfig(
  val connect: Long,
  val read: Long,
  val write: Long,
) {

  public companion object {

    private const val DEFAULT_TIMEOUT_SECONDS: Long = 10L

    public fun default(): TimeoutConfig {
      return TimeoutConfig(
        connect = DEFAULT_TIMEOUT_SECONDS,
        read = DEFAULT_TIMEOUT_SECONDS,
        write = DEFAULT_TIMEOUT_SECONDS,
      )
    }
  }
}

internal expect fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient
