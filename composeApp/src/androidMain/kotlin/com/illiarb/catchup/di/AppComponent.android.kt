package com.illiarb.catchup.di

import android.app.Application
import com.illiarb.catchup.di.scope.AppScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@[Component AppScope]
abstract class AndroidAppComponent(
  @get:Provides val application: Application
) : AppComponent {

  companion object
}
