package com.illiarb.peek

import android.app.Application
import android.content.Context
import com.illiarb.peek.di.AndroidAppComponent
import com.illiarb.peek.di.create
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

internal class PeekApp : Application() {

  private val appComponent: AndroidAppComponent by lazy {
    AndroidAppComponent.create(application = this)
  }

  override fun onCreate() {
    super.onCreate()

    Napier.base(DebugAntilog())
  }

  fun appComponent(): AndroidAppComponent = appComponent
}

internal fun Context.appComponent(): AndroidAppComponent {
  return (this as PeekApp).appComponent()
}
