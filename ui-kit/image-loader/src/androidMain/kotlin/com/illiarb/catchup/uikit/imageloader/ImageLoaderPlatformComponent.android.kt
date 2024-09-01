package com.illiarb.catchup.uikit.imageloader

import android.app.Application
import coil3.PlatformContext
import me.tatarka.inject.annotations.Provides

actual interface ImageLoaderPlatformComponent {

  @Provides
  fun providePlatformContext(application: Application): PlatformContext = application
}
