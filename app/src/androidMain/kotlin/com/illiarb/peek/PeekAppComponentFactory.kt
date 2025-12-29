package com.illiarb.peek

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

@Suppress("unused") // Referenced in manifest
@Keep
internal class PeekAppComponentFactory : AppComponentFactory() {

  override fun instantiateActivityCompat(
    cl: ClassLoader,
    className: String,
    intent: Intent?
  ): Activity {
    return getInstance(cl, className, appRef.appGraph().activityProviders)
      ?: super.instantiateActivityCompat(cl, className, intent)
  }

  override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
    val app = super.instantiateApplicationCompat(cl, className)
    appRef = app as PeekApp
    return app
  }

  private inline fun <reified T : Any> getInstance(
    cl: ClassLoader,
    className: String,
    providers: Map<KClass<out T>, Provider<T>>,
  ): T? {
    val clazz = Class.forName(className, false, cl).asSubclass(T::class.java)
    val modelProvider = providers[clazz.kotlin] ?: return null
    return modelProvider()
  }

  companion object {
    // AppComponentFactory can be created multiple times
    private lateinit var appRef: PeekApp
  }
}
