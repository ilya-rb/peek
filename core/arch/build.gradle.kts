plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.metro)
}

android {
  namespace = "com.illiarb.peek.core.arch"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
    }
  }
}

