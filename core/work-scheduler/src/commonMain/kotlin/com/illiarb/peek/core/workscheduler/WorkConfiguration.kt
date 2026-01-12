package com.illiarb.peek.core.workscheduler

import kotlin.time.Duration

public sealed interface WorkConfiguration {

  public data class PeriodicWorkConfiguration(
    val interval: Duration,
    val connectivityRequired: Boolean = false,
    val batteryNotLowRequired: Boolean = true,
  ) : WorkConfiguration
}
