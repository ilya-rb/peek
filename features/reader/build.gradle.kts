plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.kotlin.inject")
  id("com.illiarb.catchup.compose")

  alias(libs.plugins.kotlinParcelize)
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

      implementation(libs.circuit.core)

      implementation(projects.uiKit.core)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.features.reader"

  buildFeatures.compose = true

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}
