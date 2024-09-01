package com.illiarb.catchup.uikit.imageloader

import coil3.PlatformContext
import me.tatarka.inject.annotations.Provides

actual interface ImageLoaderPlatformComponent {

  @Provides
  fun providePlatformContext(): PlatformContext = PlatformContext.INSTANCE
}
