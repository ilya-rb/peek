package com.illiarb.peek

import android.app.Application
import android.content.Context
import com.illiarb.peek.core.appinfo.AppEnvironmentState
import com.illiarb.peek.core.arch.AndroidAppInitializer
import com.illiarb.peek.core.arch.AndroidAsyncAppInitializer
import com.illiarb.peek.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

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
      Napier.base(DebugAntilog())
    }

    appGraph.appInitializers
      .filterIsInstance<AndroidAppInitializer>()
      .forEach { initializer -> initializer.initialise(this) }

    appGraph.appCoroutineScope.launch {
      val initializers = appGraph.appInitializers.filterIsInstance<AndroidAsyncAppInitializer>()
      val dispatchers = appGraph.coroutineDispatchers
      val jobs = initializers.map {
        async(dispatchers.default) {
          it.initialise(this@PeekApp)
        }
      }
      jobs.awaitAll()
    }
  }
}

internal fun Context.appGraph(): AndroidAppGraph {
  return (this as PeekApp).appGraph()
}
