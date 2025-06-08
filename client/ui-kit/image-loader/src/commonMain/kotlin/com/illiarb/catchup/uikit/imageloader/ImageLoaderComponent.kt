package com.illiarb.catchup.uikit.imageloader

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.memory.MemoryCache
import coil3.util.Logger
import com.illiarb.catchup.core.appinfo.AppEnvironment
import com.illiarb.catchup.core.arch.di.AppScope
import me.tatarka.inject.annotations.Provides
import com.illiarb.catchup.core.logging.Logger as CoreLogger

public expect interface ImageLoaderPlatformComponent

public interface ImageLoaderComponent : ImageLoaderPlatformComponent {

  public val imageLoader: ImageLoader

  @Provides
  @AppScope
  public fun provideImageLoader(
    environment: AppEnvironment,
    context: PlatformContext,
    debugLogger: Logger,
  ): ImageLoader {
    return ImageLoader.Builder(context)
      .memoryCache {
        MemoryCache.Builder()
          .maxSizePercent(context, percent = 0.1)
          .build()
      }
      .apply {
        if (environment.isDev) {
          logger(debugLogger)
        }
      }
      .build()
  }

  @Provides
  public fun provideDebugLogger(): Logger = object : Logger {
    override var minLevel: Logger.Level = Logger.Level.Debug

    override fun log(tag: String, level: Logger.Level, message: String?, throwable: Throwable?) {
      @Suppress("NAME_SHADOWING")
      val message = message.orEmpty()

      when (level) {
        Logger.Level.Verbose -> CoreLogger.v(tag, throwable) { message }
        Logger.Level.Debug -> CoreLogger.d(tag, throwable) { message }
        Logger.Level.Info -> CoreLogger.i(tag, throwable) { message }
        Logger.Level.Warn -> CoreLogger.w(tag, throwable) { message }
        Logger.Level.Error -> CoreLogger.e(tag, throwable) { message }
      }
    }
  }
}
