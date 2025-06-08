package com.illiarb.peek.core.network

import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass
import io.ktor.client.HttpClient as KtorClient

public interface HttpClient {

  public suspend fun get(
    path: String,
    parameters: Map<String, String> = emptyMap(),
  ): Result<HttpResponse>

  public suspend fun post(
    path: String,
    requestBody: RequestBody<*>,
  ): Result<HttpResponse>

  public data class RequestBody<T : Any>(
    val body: T,
    val type: KClass<T>,
  ) {

    public companion object {
      public inline fun <reified T : Any> create(body: T): RequestBody<T> {
        return RequestBody(
          body = body,
          type = T::class,
        )
      }
    }
  }

  public interface Factory {

    public fun create(
      baseUrl: String,
      plugins: List<HttpClientPlugin<*, *>> = emptyList(),
    ): HttpClient
  }
}

internal class DefaultHttpClient(
  private val baseUrl: String,
  private val ktorHttpClient: KtorClient,
  private val appDispatchers: AppDispatchers,
) : HttpClient {

  override suspend fun get(
    path: String,
    parameters: Map<String, String>,
  ): Result<HttpResponse> = suspendRunCatching {
    withContext(appDispatchers.io) {
      ktorHttpClient.get(urlFor(path)) {
        contentType(ContentType.Application.Json)

        parameters.forEach { entry ->
          parameter(entry.key, entry.value)
        }
      }
    }
  }

  override suspend fun post(
    path: String,
    requestBody: HttpClient.RequestBody<*>
  ): Result<HttpResponse> = suspendRunCatching {
    withContext(appDispatchers.io) {
      ktorHttpClient.post(urlFor(path)) {
        contentType(ContentType.Application.Json)
        setBody(requestBody.body, TypeInfo(requestBody.type))
      }
    }
  }

  private fun urlFor(path: String): String {
    return "${baseUrl}/$path"
  }
}
