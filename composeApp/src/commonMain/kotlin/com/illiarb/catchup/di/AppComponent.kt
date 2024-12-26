package com.illiarb.catchup.di

import com.illiarb.catchup.core.appinfo.di.AppConfigurationComponent
import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.data.di.CoreDataComponent
import com.illiarb.catchup.core.network.di.NetworkComponent
import com.illiarb.catchup.features.settings.di.SettingsAppComponent
import com.illiarb.catchup.service.di.CatchupServiceComponent
import com.illiarb.catchup.summarizer.di.SummarizerComponent
import com.illiarb.catchup.uikit.imageloader.ImageLoaderComponent
import me.tatarka.inject.annotations.Provides

internal interface AppComponent :
  AppConfigurationComponent,
  CatchupServiceComponent,
  CoreDataComponent,
  ImageLoaderComponent,
  NetworkComponent,
  SettingsAppComponent,
  SummarizerComponent {

  @Provides
  fun provideAppDispatchers(): AppDispatchers = AppDispatchers()
}
