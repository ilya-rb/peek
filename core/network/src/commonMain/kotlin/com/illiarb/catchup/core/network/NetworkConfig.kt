package com.illiarb.catchup.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin

data class NetworkConfig(
  val apiUrl: String,
  val timeouts: TimeoutConfig,
)

data class TimeoutConfig(
  val connect: Long,
  val read: Long,
  val write: Long,
)

internal expect fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient
