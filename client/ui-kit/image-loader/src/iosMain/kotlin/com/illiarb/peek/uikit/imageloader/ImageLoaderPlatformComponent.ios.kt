package com.illiarb.peek.uikit.imageloader

import coil3.PlatformContext
import dev.zacsweers.metro.Provides

public actual interface ImageLoaderPlatformBindings {

  @Provides
  public fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE
}
