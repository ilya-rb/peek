plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlinx.serialization)

      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.stately.collections)
      implementation(libs.androidx.datastore)

      implementation(projects.core.logging)
      implementation(projects.core.coroutines)
      implementation(projects.core.arch)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.data"
}
