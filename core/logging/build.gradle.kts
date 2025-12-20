plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.napier)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.logging"
}
