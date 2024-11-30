plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.stately.collections)

      implementation(projects.core.logging)
      implementation(projects.core.coroutines)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.core.data"
}
