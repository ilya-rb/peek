import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.buildConfig)
}

android {
  namespace = "com.illiarb.catchup.core.appinfo"
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
  packageName = "com.illiarb.catchup.core.appinfo"

  defaultConfigs {
    buildConfigField(STRING, "ENV", "PROD")
  }

  defaultConfigs("dev") {
    buildConfigField(STRING, "ENV", "DEV")
  }
}
