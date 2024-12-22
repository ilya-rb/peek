package com.illiarb.catchup.core.appinfo

public enum class AppEnvironment {
  DEV,
  PROD,
  ;

  public val isDev: Boolean get() = this == DEV
}
