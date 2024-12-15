package com.illiarb.catchup.core.appinfo

public enum class AppEnvironment {
  DEV,
  PROD,
  UNSPECIFIED;

  public companion object {

    private var environment: AppEnvironment = UNSPECIFIED

    private fun checkInitialized() {
      require(environment != UNSPECIFIED) {
        "App environment must be initialized first"
      }
    }

    public fun isDev(): Boolean {
      checkInitialized()
      return environment == DEV
    }

    public fun init(newEnvironment: AppEnvironment) {
      environment = newEnvironment
    }
  }
}
