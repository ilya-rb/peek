plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.compose")
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
      implementation(libs.kotlin.inject.runtime)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.uikit.imageloader"

  buildFeatures {
    compose = true
  }

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

