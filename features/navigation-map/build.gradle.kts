plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.kotlinParcelize)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.overlay)
    }

    commonMain.dependencies {
      implementation(libs.circuit.core)
      api(libs.circuit.overlay)

      implementation(projects.core.arch)
      implementation(projects.core.types)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.navigation.map"
}