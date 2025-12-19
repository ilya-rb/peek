plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.ktor.core)
      implementation(libs.ktor.logging)
      implementation(libs.ktor.contentNegotiation)
      implementation(libs.ktor.serialization.json)

      implementation(projects.core.logging)
      implementation(projects.core.coroutines)
      implementation(projects.core.appInfo)
    }
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
    }
    iosMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.network"
}
