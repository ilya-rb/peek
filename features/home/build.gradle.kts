plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.kotlin.inject")
  id("com.illiarb.catchup.compose")

  alias(libs.plugins.kotlinParcelize)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.overlay)
    }

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.circuit.core)
      implementation(libs.circuit.overlay)

      implementation(projects.uiKit.core)
      implementation(projects.uiKit.imageLoader)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.appInfo)
      implementation(projects.catchupService)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.features.home"

  buildFeatures.compose = true

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}
