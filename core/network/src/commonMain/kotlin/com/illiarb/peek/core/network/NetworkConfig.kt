package com.illiarb.peek.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin

private const val DEFAULT_TIMEOUT_SECONDS: Long = 10L

public data class NetworkConfig(
  val timeouts: TimeoutConfig,
)

public data class TimeoutConfig(
  val connect: Long = DEFAULT_TIMEOUT_SECONDS,
  val read: Long = DEFAULT_TIMEOUT_SECONDS,
  val write: Long = DEFAULT_TIMEOUT_SECONDS,
)

internal expect fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient
