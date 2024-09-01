package com.illiarb.catchup.service.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ArticlesResponse(
  @SerialName("articles")
  val articles: List<ArticleDto>,
)
