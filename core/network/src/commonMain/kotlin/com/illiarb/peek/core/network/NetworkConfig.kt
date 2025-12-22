package com.illiarb.peek.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin

public data class NetworkConfig(
  val timeouts: TimeoutConfig,
)

public data class TimeoutConfig(
  val connect: Long,
  val read: Long,
  val write: Long,
) {

  public companion object {

    private const val DEFAULT_TIMEOUT_SECONDS: Long = 10L

    public fun default(
      connect: Long = DEFAULT_TIMEOUT_SECONDS,
      read: Long = DEFAULT_TIMEOUT_SECONDS,
      write: Long = DEFAULT_TIMEOUT_SECONDS,
    ): TimeoutConfig {
      return TimeoutConfig(connect, read, write)
    }
  }
}

internal expect fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient
