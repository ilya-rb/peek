package com.illiarb.peek.core.network

import com.illiarb.peek.core.logging.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import io.ktor.client.plugins.logging.Logger as KtorLogger

internal actual fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient {
  return HttpClient(OkHttp) {
    engine {
      config {
        retryOnConnectionFailure(true)
        connectTimeout(config.timeouts.connect, TimeUnit.SECONDS)
        readTimeout(config.timeouts.read, TimeUnit.SECONDS)
        writeTimeout(config.timeouts.write, TimeUnit.SECONDS)
      }
    }

    plugins.forEach { plugin ->
      install(plugin)
    }

    install(ContentNegotiation) {
      json(Json { ignoreUnknownKeys = true })
    }

    install(Logging) {
      level = LogLevel.ALL
      logger = object : KtorLogger {
        override fun log(message: String) {
          Logger.v(tag = "AndroidHttpClient") { message }
        }
      }
    }
  }
}
