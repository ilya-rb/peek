package com.illiarb.peek.init

import android.content.Context
import android.os.Build
import android.os.StrictMode
import com.illiarb.peek.core.arch.di.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@Inject
@ContributesIntoSet(AppScope::class, binding = binding<AndroidAppInitializer>())
@Suppress("unused")
internal class StrictModeInitializer : AndroidAppInitializer {

  override val async: Boolean = false

  override val key: String = "StrictModeInitializer"

  override suspend fun initialise(context: Context) {
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
