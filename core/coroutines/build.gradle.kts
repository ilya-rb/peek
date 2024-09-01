plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
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
  namespace = "com.illiarb.catchup.core.coroutines"
}
