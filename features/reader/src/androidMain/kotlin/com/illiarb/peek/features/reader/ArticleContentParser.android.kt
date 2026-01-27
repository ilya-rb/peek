package com.illiarb.peek.features.reader

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dankito.readability4j.extended.Readability4JExtended

internal actual class ArticleContentParser actual constructor() {

  private val httpClient = HttpClient(OkHttp)

  actual suspend fun parse(url: String): HtmlContent? {
    return withContext(Dispatchers.IO) {
      try {
        val html = httpClient.get(url).bodyAsText()
        val readability = Readability4JExtended(url, html)
        val article = readability.parse()
        val htmlContent = article.contentWithDocumentsCharsetOrUtf8
        if (htmlContent.isNullOrEmpty()) {
          null
        } else {
          HtmlContent(htmlContent)
        }
      } catch (_: Exception) {
        null
      }
    }
  }
}
