plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.napier)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.logging"
}
