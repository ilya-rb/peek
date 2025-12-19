plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinParcelize)
}

android {
  namespace = "com.illiarb.peek.core.types"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(projects.core.arch)
    }
  }
}

