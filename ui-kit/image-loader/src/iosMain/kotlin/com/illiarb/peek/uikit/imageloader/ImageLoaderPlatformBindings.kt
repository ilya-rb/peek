package com.illiarb.peek.uikit.imageloader

import coil3.PlatformContext
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
public actual object ImageLoaderPlatformBindings {

  @Provides
  public fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE
}
