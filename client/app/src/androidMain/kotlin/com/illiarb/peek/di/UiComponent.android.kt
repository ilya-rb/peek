package com.illiarb.peek.di

import android.app.Activity
import com.illiarb.peek.di.scope.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
internal abstract class AndroidUiComponent(
  @Component val appComponent: AndroidAppComponent,
  @get:Provides val activity: Activity,
) : UiComponent {

  companion object
}
