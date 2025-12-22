plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidCacheFixPlugin) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.buildConfig) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.detekt) apply false
  alias(libs.plugins.jetbrainsCompose) apply false
  alias(libs.plugins.kotlinAtomic) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.kotlinParcelize) apply false
  alias(libs.plugins.kotlinSerialization) apply false
  alias(libs.plugins.stabilityAnalyzer) apply false

  alias(libs.plugins.doctor)
}

doctor {
  GCWarningThreshold.set(0.10f)
  allowBuildingAllAndroidAppsSimultaneously.set(false)
  disallowMultipleDaemons.set(true)
  negativeAvoidanceThreshold.set(500)
  warnIfKotlinCompileDaemonFallback.set(true)
  warnWhenJetifierEnabled.set(true)
  // Check
  disallowMultipleDaemons.set(false)

  javaHome {
    ensureJavaHomeMatches.set(true)
    ensureJavaHomeIsSet.set(true)
    failOnError.set(true)
  }
}
