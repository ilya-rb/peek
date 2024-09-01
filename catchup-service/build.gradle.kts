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
      implementation(libs.ktor.serialization.json)

      implementation(projects.core.network)
      implementation(projects.core.logging)
      implementation(projects.core.data)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.service"
}
