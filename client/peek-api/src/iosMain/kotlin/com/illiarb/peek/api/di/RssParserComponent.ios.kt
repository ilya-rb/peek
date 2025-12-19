package com.illiarb.peek.api.di

import com.illiarb.peek.core.arch.di.AppScope
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

public actual interface RssParserBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideRssParser(): RssParser {
    return RssParserBuilder().build()
  }
}