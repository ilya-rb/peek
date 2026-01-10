package com.illiarb.peek

import android.app.Application
import android.content.Context
import com.illiarb.peek.core.appinfo.AppEnvironmentState
import com.illiarb.peek.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

internal class PeekApp : Application() {

  private val appGraph: AndroidAppGraph by lazy {
    createGraphFactory<AndroidAppGraph.Factory>().create(this)
  }
  private val appScope = CoroutineScope(SupervisorJob())

  override fun onCreate() {
    super.onCreate()
    setup()
  }

  fun appGraph(): AndroidAppGraph = appGraph

  fun setup() {
    if (AppEnvironmentState.isDev()) {
      Napier.base(DebugAntilog())
    }

    appScope.launch {
      val (regular, async) = appGraph.androidAppInitializers.partition { it.async }
      val dispatchers = appGraph.appDispatchers

      regular.forEach {
        it.initialise(this@PeekApp)
      }

      val jobs = async.map {
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
