plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
    }
    androidMain.dependencies {
      implementation(libs.kotlin.coroutines.android)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.coroutines"
}
