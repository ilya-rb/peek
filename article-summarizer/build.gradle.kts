import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  id("com.illiarb.catchup.android.library")
  id("com.illiarb.catchup.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.buildConfig)
}

kotlin {
  sourceSets {
    androidMain.dependencies {
      implementation(libs.sqldelight.android)
    }

    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlin.inject.runtime)
      implementation(libs.kotlinx.datetime)
      implementation(libs.ktor.core)
      implementation(libs.ktor.serialization.json)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)

      implementation(projects.core.network)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native)
    }
  }
}

android {
  namespace = "com.illiarb.catchup.summarizer"
}

sqldelight {
  databases {
    create("Database") {
      packageName = "com.illiarb.catchup.summarizer"
      schemaOutputDirectory.set(File("src/commonMain/sqldelight/databases"))
    }
  }
}

buildkonfig {
  packageName = "com.illiarb.catchup.summarizer"

  defaultConfigs {
    val openAIKey = gradleLocalProperties(rootDir, providers)[("openai.api_key")]
    require(openAIKey is String) { "Cannot find Open AI API key" }

    buildConfigField(STRING, "OPENAI_KEY", openAIKey)
    buildConfigField(STRING, "OPENAI_URL", "https://api.openai.com")
  }
}

