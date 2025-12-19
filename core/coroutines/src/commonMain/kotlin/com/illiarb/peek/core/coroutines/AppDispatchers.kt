package com.illiarb.peek.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

public data class AppDispatchers(
  val default: CoroutineDispatcher = Dispatchers.Default,
  val io: CoroutineDispatcher = Dispatchers.IO,
  val main: CoroutineDispatcher = Dispatchers.Main,
  val unconfined: CoroutineDispatcher = Dispatchers.Unconfined,
)
