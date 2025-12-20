import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.metro)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.sqldelight.android)
    }

    commonMain.dependencies {
      implementation(libs.circuit.core)
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.ktor.core)
      implementation(libs.ktor.serialization.json)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)

      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.data)
      implementation(projects.core.logging)
      implementation(projects.core.network)
      implementation(projects.core.types)
      implementation(projects.features.navigationMap)
      implementation(projects.peekApi)
      implementation(projects.uiKit.core)
      implementation(projects.uiKit.resources)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.summarizer"
}

sqldelight {
  databases {
    create("Database") {
      packageName = "com.illiarb.peek.features.summarizer"
      schemaOutputDirectory.set(File("src/commonMain/sqldelight/databases"))
    }
  }
}

buildkonfig {
  packageName = "com.illiarb.peek.features.summarizer"

  defaultConfigs {
    val openAIKey = gradleLocalProperties(rootDir, providers)[("openai.api_key")]
    require(openAIKey is String) { "Cannot find Open AI API key" }

    buildConfigField(STRING, "OPENAI_KEY", openAIKey)
    buildConfigField(STRING, "OPENAI_URL", "https://api.openai.com")
  }
}

