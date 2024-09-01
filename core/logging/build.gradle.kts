plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.napier)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.core.logging"
}
