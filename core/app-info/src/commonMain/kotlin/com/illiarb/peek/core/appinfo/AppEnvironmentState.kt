package com.illiarb.peek.core.appinfo

public object AppEnvironmentState {

  internal val current: AppEnvironment = runCatching {
    AppEnvironment.valueOf(BuildKonfig.ENV)
  }.getOrElse {
    AppEnvironment.UNDEFINED
  }

  public fun isDev(): Boolean = current == AppEnvironment.DEV
}

public enum class AppEnvironment {
  DEV,
  PROD,
  UNDEFINED,
}
