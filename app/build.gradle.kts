plugins {
  alias(libs.plugins.peek.android.application)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
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
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.browser)
      implementation(libs.circuit.core)
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.gestureNavigation)
      implementation(libs.circuit.x.overlay)
    }

    commonMain.dependencies {
      implementation(libs.circuit.core)
      implementation(libs.coil.compose)
      implementation(libs.ktor.core)
      implementation(libs.napier)
      implementation(libs.rssparser)

      implementation(projects.core.appInfo)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.data)
      implementation(projects.core.logging)
      implementation(projects.core.network)
      implementation(projects.core.types)
      implementation(projects.features.home)
      implementation(projects.features.navigationMap)
      implementation(projects.features.reader)
      implementation(projects.features.settings)
      implementation(projects.features.summarizer)
      implementation(projects.peekApi)
      implementation(projects.uiKit.core)
      implementation(projects.uiKit.imageLoader)
      implementation(projects.uiKit.resources)
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

  signingConfigs {

    getByName("debug") {
      storeFile = rootProject.file("release/app-debug.jks")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }

    if (rootProject.file("relase/app-release.jks").exists()) {
      create("release") {
        storeFile = rootProject.file("releaase/app-release.jks")
        storePassword = properties["PEEK_RELEASE_KEYSTORE_PWD"]?.toString().orEmpty()
        keyAlias = "peek"
        keyPassword = properties["PEEK_RELEASE_KEY_PWD"]?.toString().orEmpty()
      }
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      signingConfig = signingConfigs.findByName("release") ?: signingConfigs["debug"]
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}



