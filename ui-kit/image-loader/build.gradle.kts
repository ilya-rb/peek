plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.appInfo)
      implementation(projects.core.logging)
      implementation(projects.core.arch)

      implementation(libs.coil.network)
      implementation(libs.coil.compose)
    }
  }
}

android {
  namespace = "com.illiarb.peek.uikit.imageloader"
}

