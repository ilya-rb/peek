package com.illiarb.catchup.di

import android.app.Activity
import com.illiarb.catchup.di.scope.ViewControllerScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@[Component ViewControllerScope]
abstract class AndroidUiComponent(
  @Component val appComponent: AppComponent,
  @get:Provides val activity: Activity,
) : UiComponent {

  companion object
}
