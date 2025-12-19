plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")

  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

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

  buildFeatures {
    compose = true
  }

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

