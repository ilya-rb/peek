plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")

  alias(libs.plugins.kotlinParcelize)
}

android {
  namespace = "com.illiarb.catchup.core.arch"
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.circuit.x.android)
    }
    commonMain.dependencies {
      implementation(libs.circuit.core)
      implementation(libs.kotlin.inject.runtime)
    }
  }
}

