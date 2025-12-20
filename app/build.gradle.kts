plugins {
  id("com.illiarb.peek.android.application")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")

  alias(libs.plugins.metro)
}

kotlin {
  listOf(
    iosArm64(),
    iosSimulatorArm64()
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "Peek"
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
      implementation(libs.circuit.x.overlay)
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
      implementation(libs.rssparser)

      implementation(projects.peekApi)
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
      implementation(projects.summarizer)
    }
  }
}

android {
  namespace = "com.illiarb.peek"

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  defaultConfig {
    applicationId = "com.illiarb.peek"
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



