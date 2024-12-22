plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
}

android {
  namespace = "com.illiarb.catchup.core.appinfo"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.kotlin.coroutines.core)

      implementation(projects.core.data)
      implementation(projects.core.arch)
    }
  }
}
