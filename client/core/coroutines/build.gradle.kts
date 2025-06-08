plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
    }
    androidMain.dependencies {
      implementation(libs.kotlin.coroutines.android)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.coroutines"
}
