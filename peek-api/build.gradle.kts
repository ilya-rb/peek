plugins {
  id("com.illiarb.peek.android.library")
  id("com.illiarb.peek.kotlin.multiplatform")

  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.buildConfig)
  alias(libs.plugins.metro)
}

kotlin {
  compilerOptions.optIn.add("kotlin.time.ExperimentalTime")

  sourceSets {
    androidMain.dependencies {
      implementation(libs.sqldelight.android)
    }

    commonMain.dependencies {
      implementation(libs.kotlin.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.ktor.core)
      implementation(libs.ktor.serialization.json)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)
      implementation(libs.rssparser)

      implementation(projects.core.network)
      implementation(projects.core.logging)
      implementation(projects.core.data)
      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.types)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native)
    }
  }
}

android {
  namespace = "com.illiarb.peek.api"
}

sqldelight {
  databases {
    create("Database") {
      packageName = "com.illiarb.peek.api"
      schemaOutputDirectory.set(File("src/commonMain/sqldelight/databases"))
    }
  }
}

buildkonfig {
  packageName = "com.illiarb.peek.core.network"

  defaultConfigs {
  }
}


