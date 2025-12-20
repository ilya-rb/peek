plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.browser)
    }

    commonMain.dependencies {
      implementation(libs.kotlinx.datetime)
      implementation(libs.androidx.webkit)
      implementation(libs.circuit.core)
      implementation(libs.circuit.overlay)
      implementation(libs.coil.network)
      implementation(libs.coil.compose)
      implementation(libs.shimmer)
      implementation(libs.lottie.core)

      implementation(projects.core.logging)
      implementation(projects.core.appInfo)
      implementation(projects.uiKit.imageLoader)
      implementation(projects.uiKit.resources)
    }
  }
}

android {
  namespace = "com.illiarb.peek.uikit.core"
}

