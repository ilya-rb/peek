plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.core.data"
}
