plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.kotlinAtomic)
  alias(libs.plugins.kotlinKsp)
  alias(libs.plugins.metro)
}

android {
  namespace = "com.illiarb.peek.core.data"
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

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.turbine)
    }
  }
}
