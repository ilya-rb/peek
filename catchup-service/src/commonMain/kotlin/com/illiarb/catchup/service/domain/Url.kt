package com.illiarb.catchup.service.domain

public data class Url(val url: String) {
  init {
    require(url.isNotEmpty())
  }
}