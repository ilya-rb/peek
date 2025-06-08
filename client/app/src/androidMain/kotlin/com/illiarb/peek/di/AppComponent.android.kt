package com.illiarb.peek.di

import android.app.Application
import com.illiarb.peek.core.arch.di.AppScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@AppScope
internal abstract class AndroidAppComponent(
  @get:Provides val application: Application
) : AppComponent {

  companion object
}
