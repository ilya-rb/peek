plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
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

