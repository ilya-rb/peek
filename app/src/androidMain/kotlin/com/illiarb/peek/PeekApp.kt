package com.illiarb.peek

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import com.illiarb.peek.core.appinfo.AppEnvironmentState
import com.illiarb.peek.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

internal class PeekApp : Application() {

  private val appGraph: AndroidAppGraph by lazy {
    createGraphFactory<AndroidAppGraph.Factory>().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    setup()
  }

  fun appGraph(): AndroidAppGraph = appGraph

  fun setup() {
    if (AppEnvironmentState.isDev()) {
      setupStrictMode()

      Napier.base(DebugAntilog())
    }
  }

  private fun setupStrictMode() {
    StrictMode.setThreadPolicy(
      StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build(),
    )

    StrictMode.setVmPolicy(
      StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .detectActivityLeaks()
        .detectLeakedClosableObjects()
        .detectLeakedRegistrationObjects()
        .detectFileUriExposure()
        .detectCleartextNetwork()
        .apply {
          detectContentUriWithoutPermission()

          if (Build.VERSION.SDK_INT >= 29) {
            detectCredentialProtectedWhileLocked()
          }
          if (Build.VERSION.SDK_INT >= 31) {
            detectIncorrectContextUse()
            detectUnsafeIntentLaunch()
          }
        }
        .penaltyLog()
        .build()
    )
  }
}

internal fun Context.appGraph(): AndroidAppGraph {
  return (this as PeekApp).appGraph()
}
