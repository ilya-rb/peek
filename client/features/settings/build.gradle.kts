plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")

  alias(libs.plugins.kotlinParcelize)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(compose.ui)
      implementation(compose.components.resources)

      implementation(libs.circuit.core)
      implementation(libs.kotlin.inject.runtime)

      implementation(projects.uiKit.core)
      implementation(projects.uiKit.resources)

      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.appInfo)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.settings"

  buildFeatures.compose = true

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}
