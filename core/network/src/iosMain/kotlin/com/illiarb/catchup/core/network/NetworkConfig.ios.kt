package com.illiarb.catchup.core.network

import com.illiarb.catchup.core.logging.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger as KtorLogger
import io.ktor.client.plugins.HttpClientPlugin

internal actual fun createKtorClient(
  config: NetworkConfig,
  plugins: List<HttpClientPlugin<*, *>>,
): HttpClient {
  return HttpClient(Darwin) {
    engine {
      configureRequest {
        setAllowsCellularAccess(true)
      }
    }

    install(Logging) {
      level = LogLevel.HEADERS
      logger = object : KtorLogger {
        override fun log(message: String) {
          Logger.v(tag = "iOSHttpClient") { message }
        }
      }
    }

    install(ContentNegotiation) {
      json(Json {
        prettyPrint = true
      })
    }
  }
}
