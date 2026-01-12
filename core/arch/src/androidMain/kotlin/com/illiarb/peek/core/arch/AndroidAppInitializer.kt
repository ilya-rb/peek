package com.illiarb.peek.core.arch

import android.content.Context

public interface AndroidAppInitializer : AppInitializer {

  public fun initialise(context: Context)
}

public interface AndroidAsyncAppInitializer : AppInitializer {

  public suspend fun initialise(context: Context)
}
