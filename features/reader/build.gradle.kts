plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.circuit.core)

      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.data)
      implementation(projects.core.logging)
      implementation(projects.core.types)
      implementation(projects.features.navigationMap)
      implementation(projects.peekApi)
      implementation(projects.uiKit.core)
      implementation(projects.uiKit.resources)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.reader"
}
