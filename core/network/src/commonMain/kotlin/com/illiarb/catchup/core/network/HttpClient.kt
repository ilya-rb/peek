package com.illiarb.catchup.core.network

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.coroutines.suspendRunCatching
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.withContext
import io.ktor.client.HttpClient as KtorClient

interface HttpClient {

  suspend fun get(
    path: String,
    parameters: Map<String, String> = emptyMap(),
  ): Result<HttpResponse>
}

internal class DefaultHttpClient(
  private val baseUrl: String,
  private val ktorHttpClient: KtorClient,
  private val appDispatchers: AppDispatchers,
) : HttpClient {

  override suspend fun get(path: String, parameters: Map<String, String>): Result<HttpResponse> =
    suspendRunCatching {
      withContext(appDispatchers.io) {
        ktorHttpClient.get(urlFor(path)) {
          contentType(ContentType.Application.Json)

          parameters.forEach { entry ->
            parameter(entry.key, entry.value)
          }
        }
      }
    }

  private fun urlFor(path: String): String {
    return "${baseUrl}/$path"
  }
}
