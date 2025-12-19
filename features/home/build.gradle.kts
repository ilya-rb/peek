import com.illiarb.peek.gradle.addKspDependencyForAllTargets

plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")

  alias(libs.plugins.kotlinParcelize)
  alias(libs.plugins.metro)
  alias(libs.plugins.kotlinKsp)
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
      implementation(compose.materialIconsExtended)

      implementation(libs.circuit.core)
      implementation(libs.circuit.overlay)
      implementation(libs.circuit.codegen.annotations)

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
      implementation(projects.summarizer)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.home"

  buildFeatures.compose = true

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

ksp {
  arg("circuit.codegen.mode", "metro")
}

addKspDependencyForAllTargets(libs.circuit.codegen)