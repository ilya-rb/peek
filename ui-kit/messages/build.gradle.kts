plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.circuit.overlay)
      implementation(libs.kotlin.coroutines.core)

      implementation(projects.core.arch)
    }
  }
}

android {
  namespace = "com.illiarb.peek.uikit.messages"
}
