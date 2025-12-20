plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
}

android {
  namespace = "com.illiarb.peek.uikit.resources"

  sourceSets["main"].resources.srcDirs("src/commonMain/composeResources")
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.illiarb.peek.uikit.resources"
  generateResClass = always
}

