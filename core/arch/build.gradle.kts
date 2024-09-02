plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.kotlin.inject")

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
    }
  }
}

