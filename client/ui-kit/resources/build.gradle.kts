plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")
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
  namespace = "com.illiarb.peek.uikit.resources"

  sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")

  buildFeatures {
    compose = true
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.illiarb.peek.uikit.resources"
  generateResClass = always
}

