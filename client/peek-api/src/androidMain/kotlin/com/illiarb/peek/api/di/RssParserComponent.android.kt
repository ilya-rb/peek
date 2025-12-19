package com.illiarb.peek.api.di

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.logging.Logger
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

@BindingContainer
public actual object RssParserBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(LoggingInterceptor())
      .build()
  }

  @Provides
  @SingleIn(AppScope::class)
  public fun provideRssParser(client: OkHttpClient): RssParser {
    return RssParserBuilder(
      callFactory = { request ->
        client.newCall(request)
      }
    ).build()
  }

  private class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      Logger.i(TAG) { "Request: \nURL:${request.url}\nHEADERS:\n${request.headers}" }

      val response = chain.proceed(request)
      Logger.i(TAG) { "Response: \nURL:${request.url}\nHEADERS:\n${response.headers}" }

      return response
    }

    companion object {
      const val TAG = "Network"
    }
  }
}