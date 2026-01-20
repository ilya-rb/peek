plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.napier)
      implementation(libs.kotlin.coroutines.core)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.logging"
}
