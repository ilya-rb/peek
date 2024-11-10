package com.illiarb.catchup.core.appinfo

enum class AppEnvironment {
  DEV,
  PROD,
  UNSPECIFIED;

  companion object {

    private var environment: AppEnvironment = UNSPECIFIED

    private fun checkInitialized() {
      require(environment != UNSPECIFIED) {
        "App environment must be initialized first"
      }
    }

    fun isDev(): Boolean {
      checkInitialized()
      return environment == DEV
    }

    fun init(newEnvironment: AppEnvironment) {
      environment = newEnvironment
    }
  }
}
