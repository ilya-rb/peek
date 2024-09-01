plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.kotlin.inject")

  alias(libs.plugins.kotlinSerialization)
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
  namespace = "com.illiarb.catchup.core.network"
}
