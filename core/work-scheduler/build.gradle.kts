plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.metro)
}

android {
  namespace = "com.illiarb.peek.core.workscheduler"
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.androidx.work)
    }

    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)

      implementation(projects.core.arch)
      implementation(projects.core.logging)
    }
  }
}

