package com.illiarb.peek.di

import android.content.Context
import coil3.ImageLoader
import com.illiarb.peek.api.di.PeekApiBindings
import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.di.AppConfigurationsBindings
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.data.di.CoreDataBindings
import com.illiarb.peek.core.network.di.NetworkBindings
import com.illiarb.peek.features.settings.data.SettingsService
import com.illiarb.peek.features.settings.di.AppSettingsBindings
import com.illiarb.peek.summarizer.di.SummarizerBindings
import com.illiarb.peek.uikit.imageloader.ImageLoaderBindings
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
  scope = AppScope::class,
  bindingContainers = [
    CoreDataBindings::class,
    NetworkBindings::class,
    PeekApiBindings::class,
    AppConfigurationsBindings::class,
    AppSettingsBindings::class,
    ImageLoaderBindings::class,
    SummarizerBindings::class,
  ]
)
internal interface AndroidAppGraph : AppGraph {

  val uiGraph: AndroidUiGraph.Factory

  val appConfiguration: AppConfiguration
  val settingsService: SettingsService
  val imageLoader: ImageLoader

  @Provides
  fun provideAppDispatchers(): AppDispatchers = AppDispatchers()

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides context: Context): AndroidAppGraph
  }
}
