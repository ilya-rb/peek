package com.illiarb.peek.core.appinfo

public object AppEnvironmentState {

  internal var current: AppEnvironment = AppEnvironment.valueOf(BuildKonfig.ENV)

  public fun isDev(): Boolean = current == AppEnvironment.DEV
}

public enum class AppEnvironment {
  DEV,
  PROD,
  UNDEFINED,
}
