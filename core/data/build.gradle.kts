plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.kotlinAtomic)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      api(libs.kotlinx.serialization)
      api(libs.sqldelight.primitive)

      implementation(libs.androidx.datastore)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.stately.collections)

      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.logging)
      implementation(projects.core.types)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.kotlin.coroutines.test)
      implementation(libs.turbine)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.data"
}
