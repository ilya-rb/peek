plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinParcelize)
  alias(libs.plugins.metro)
}

android {
  namespace = "com.illiarb.peek.core.arch"
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

