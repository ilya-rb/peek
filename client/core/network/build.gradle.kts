import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.buildConfig)
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.ktor.core)
      implementation(libs.ktor.logging)
      implementation(libs.ktor.contentNegotiation)
      implementation(libs.ktor.serialization.json)

      implementation(projects.core.logging)
      implementation(projects.core.coroutines)
      implementation(projects.core.appInfo)
    }
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
    }
    iosMain.dependencies {
      implementation(libs.ktor.client.darwin)
    }
  }
}

android {
  namespace = "com.illiarb.peek.core.network"
}

buildkonfig {
  packageName = "com.illiarb.peek.core.network"

  defaultConfigs {
    buildConfigField(STRING, "API_URL", "https://peek-dzdqhg.fly.dev")
  }
}
