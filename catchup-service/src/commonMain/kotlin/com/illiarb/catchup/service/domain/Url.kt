package com.illiarb.catchup.service.domain

data class Url(val url: String) {
  init {
    require(url.isNotEmpty())
  }
}