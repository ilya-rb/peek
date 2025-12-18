import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")
  id("com.illiarb.peek.compose")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.kotlinParcelize)
  alias(libs.plugins.metro)
}

kotlin {
  compilerOptions.optIn.add("kotlin.time.ExperimentalTime")

  sourceSets {
    androidMain.dependencies {
      implementation(libs.sqldelight.android)
      implementation(libs.circuit.x.android)
      implementation(libs.circuit.x.overlay)
    }

    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.materialIconsExtended)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.circuit.core)
      implementation(libs.circuit.overlay)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.kotlinx.datetime)
      implementation(libs.ktor.core)
      implementation(libs.ktor.serialization.json)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)

      implementation(projects.uiKit.core)
      implementation(projects.uiKit.resources)

      implementation(projects.core.network)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.types)
      implementation(projects.peekApi)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native)
    }
  }
}

android {
  namespace = "com.illiarb.peek.summarizer"

  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

sqldelight {
  databases {
    create("Database") {
      packageName = "com.illiarb.peek.summarizer"
      schemaOutputDirectory.set(File("src/commonMain/sqldelight/databases"))
    }
  }
}

buildkonfig {
  packageName = "com.illiarb.peek.summarizer"

  defaultConfigs {
    val openAIKey = gradleLocalProperties(rootDir, providers)[("openai.api_key")]
    require(openAIKey is String) { "Cannot find Open AI API key" }

    buildConfigField(STRING, "OPENAI_KEY", openAIKey)
    buildConfigField(STRING, "OPENAI_URL", "https://api.openai.com")
  }
}

