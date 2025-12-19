package com.illiarb.peek.uikit.imageloader

import android.app.Application
import coil3.PlatformContext
import dev.zacsweers.metro.Provides

public actual interface ImageLoaderPlatformBindings {

  @Provides
  public fun providePlatformContext(application: Application): PlatformContext = application
}
