plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")
  id("com.illiarb.catchup.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.components.resources)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.uikit.resources"

  sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")

  buildFeatures {
    compose = true
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.illiarb.catchup.uikit.resources"
  generateResClass = always
}

