import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.metro)
}

android {
  namespace = "com.illiarb.peek.core.appinfo"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.kotlin.coroutines.core)

      implementation(projects.core.data)
      implementation(projects.core.arch)
    }
  }
}

buildkonfig {
  packageName = "com.illiarb.peek.core.appinfo"

  defaultConfigs {
    buildConfigField(STRING, "ENV", "PROD")
  }

  defaultConfigs("dev") {
    buildConfigField(STRING, "ENV", "DEV")
  }
}
