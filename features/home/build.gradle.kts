plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.kotlinParcelize)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.overlay)
    }

    commonMain.dependencies {
      implementation(libs.circuit.core)
      implementation(libs.circuit.overlay)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.collections)
      implementation(libs.haze)
      implementation(libs.haze.materials)

      implementation(projects.uiKit.core)
      implementation(projects.uiKit.imageLoader)
      implementation(projects.uiKit.resources)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.appInfo)
      implementation(projects.core.types)
      implementation(projects.peekApi)
      implementation(projects.features.reader)
      implementation(projects.features.settings)
      implementation(projects.features.summarizer)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.home"
}