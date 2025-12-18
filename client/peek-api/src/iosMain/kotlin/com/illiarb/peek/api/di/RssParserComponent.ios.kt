package com.illiarb.peek.api.di

import com.illiarb.peek.core.arch.di.AppScope
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import me.tatarka.inject.annotations.Provides

public actual interface RssParserComponent {

  @Provides
  @AppScope
  public fun provideRssParser(): RssParser {
    return RssParserBuilder().build()
  }
}