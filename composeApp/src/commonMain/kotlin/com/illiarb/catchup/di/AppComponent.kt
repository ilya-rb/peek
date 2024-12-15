package com.illiarb.catchup.di

import coil3.ImageLoader
import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.network.di.NetworkComponent
import com.illiarb.catchup.features.settings.data.SettingsService
import com.illiarb.catchup.features.settings.di.SettingsAppComponent
import com.illiarb.catchup.service.CatchupService
import com.illiarb.catchup.service.di.CatchupServiceComponent
import com.illiarb.catchup.uikit.imageloader.ImageLoaderComponent
import me.tatarka.inject.annotations.Provides

internal interface AppComponent :
  NetworkComponent,
  CatchupServiceComponent,
  ImageLoaderComponent,
  SettingsAppComponent {

  val settingsService: SettingsService
  val imageLoader: ImageLoader
  val catchupService: CatchupService

  @Provides
  fun provideAppDispatchers(): AppDispatchers = AppDispatchers()
}
