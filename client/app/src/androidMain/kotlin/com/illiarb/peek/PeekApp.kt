package com.illiarb.peek

import android.app.Application
import android.content.Context
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

    Napier.base(DebugAntilog())
  }

  fun appGraph(): AndroidAppGraph = appGraph
}

internal fun Context.appGraph(): AndroidAppGraph {
  return (this as PeekApp).appGraph()
}
