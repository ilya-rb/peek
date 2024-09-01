package com.illiarb.catchup.di

import com.illiarb.catchup.core.coroutines.AppDispatchers
import com.illiarb.catchup.core.network.di.NetworkComponent
import com.illiarb.catchup.service.di.CatchupServiceComponent
import com.illiarb.catchup.uikit.imageloader.ImageLoaderComponent
import me.tatarka.inject.annotations.Provides

interface AppComponent : NetworkComponent, CatchupServiceComponent, ImageLoaderComponent {

  @Provides
  fun provideAppDispatchers(): AppDispatchers = AppDispatchers()
}
