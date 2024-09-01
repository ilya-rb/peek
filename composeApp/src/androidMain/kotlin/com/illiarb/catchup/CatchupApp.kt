package com.illiarb.catchup

import android.app.Application
import android.content.Context
import com.illiarb.catchup.core.appinfo.AppEnvironment
import com.illiarb.catchup.di.AndroidAppComponent
import com.illiarb.catchup.di.AppComponent
import com.illiarb.catchup.di.create
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class CatchupApp : Application() {

  private val appComponent: AppComponent by lazy {
    AndroidAppComponent::class.create(application = this)
  }

  override fun onCreate() {
    super.onCreate()

    AppEnvironment.init(AppEnvironment.DEV)

    Napier.base(DebugAntilog())
  }

  fun appComponent(): AppComponent = appComponent
}

fun Context.appComponent(): AppComponent {
  return (this as CatchupApp).appComponent()
}
