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
      api(libs.circuit.overlay)

      implementation(libs.circuit.core)

      implementation(projects.core.arch)
      implementation(projects.core.types)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.navigation.map"
}