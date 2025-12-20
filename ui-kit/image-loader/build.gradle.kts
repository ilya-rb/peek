plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.coil.compose)
      implementation(libs.coil.network)

      implementation(projects.core.appInfo)
      implementation(projects.core.arch)
      implementation(projects.core.logging)
    }
  }
}

android {
  namespace = "com.illiarb.peek.uikit.imageloader"
}

