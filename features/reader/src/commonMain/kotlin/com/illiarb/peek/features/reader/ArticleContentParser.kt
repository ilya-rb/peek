package com.illiarb.peek.features.reader

import kotlin.jvm.JvmInline

@JvmInline
internal value class HtmlContent(val content: String)

internal expect class ArticleContentParser() {
  suspend fun parse(url: String): HtmlContent?
}
