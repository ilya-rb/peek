package com.illiarb.peek.api.domain

public data class Url(val url: String) {
  init {
    require(url.isNotEmpty())
  }
}