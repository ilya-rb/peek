import com.illiarb.catchup.gradle.addKspDependencyForAllTargets

plugins {
  id("com.illiarb.catchup.android.application")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.compose")

  alias(libs.plugins.kotlinKsp)
}

kotlin {
  listOf(
    iosArm64(),
    iosSimulatorArm64()
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.browser)
      implementation(libs.circuit.core)
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.gestureNavigation)
    }

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.circuit.core)
      implementation(libs.ktor.core)
      implementation(libs.napier)
      implementation(libs.coil.compose)
      implementation(libs.kotlin.inject.runtime)

      implementation(projects.catchupService)
      implementation(projects.core.appInfo)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
      implementation(projects.core.network)
      implementation(projects.core.data)
      implementation(projects.features.home)
      implementation(projects.features.reader)
      implementation(projects.features.settings)
      implementation(projects.uiKit.core)
      implementation(projects.uiKit.imageLoader)
      implementation(projects.articleSummarizer)
    }
  }
}

android {
  namespace = "com.illiarb.catchup"

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  defaultConfig {
    applicationId = "com.illiarb.catchup"
    versionCode = 1
    versionName = "1.0"
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }

  buildFeatures {
    compose = true
  }

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

ksp {
  arg("me.tatarka.inject.dumpGraph", "true")
  arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

addKspDependencyForAllTargets(libs.kotlin.inject.compiler)



