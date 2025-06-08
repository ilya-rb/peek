package com.illiarb.peek.di

import com.illiarb.peek.core.appinfo.di.AppConfigurationComponent
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.data.di.CoreDataComponent
import com.illiarb.peek.core.network.di.NetworkComponent
import com.illiarb.peek.features.settings.di.SettingsAppComponent
import com.illiarb.peek.api.di.PeekApiComponent
import com.illiarb.peek.summarizer.di.SummarizerComponent
import com.illiarb.peek.uikit.imageloader.ImageLoaderComponent
import me.tatarka.inject.annotations.Provides

internal interface AppComponent :
  AppConfigurationComponent,
  PeekApiComponent,
  CoreDataComponent,
  ImageLoaderComponent,
  NetworkComponent,
  SettingsAppComponent,
  SummarizerComponent {

  @Provides
  fun provideAppDispatchers(): AppDispatchers = AppDispatchers()
}
