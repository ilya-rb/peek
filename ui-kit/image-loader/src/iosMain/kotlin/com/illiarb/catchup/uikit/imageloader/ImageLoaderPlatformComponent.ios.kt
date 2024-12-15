package com.illiarb.catchup.uikit.imageloader

import coil3.PlatformContext
import me.tatarka.inject.annotations.Provides

public actual interface ImageLoaderPlatformComponent {

  @Provides
  public fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE
}
