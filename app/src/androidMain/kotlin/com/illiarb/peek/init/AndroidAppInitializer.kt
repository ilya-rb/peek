package com.illiarb.peek.init

import android.content.Context
import com.illiarb.peek.core.arch.AppInitializer

internal interface AndroidAppInitializer : AppInitializer {

  suspend fun initialise(context: Context)
}
