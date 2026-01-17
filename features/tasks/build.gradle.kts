plugins {
  alias(libs.plugins.peek.android.library)
  alias(libs.plugins.peek.multiplatform)
  alias(libs.plugins.peek.compose)
  alias(libs.plugins.sqldelight)
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
      implementation(libs.kotlinx.collections)
      implementation(libs.kotlinx.datetime)
      implementation(libs.sqldelight.coroutines)
      implementation(libs.sqldelight.primitive)

      implementation(projects.core.arch)
      implementation(projects.core.coroutines)
      implementation(projects.core.data)
      implementation(projects.core.logging)
      implementation(projects.core.types)
      implementation(projects.features.navigationMap)
      implementation(projects.uiKit.core)
      implementation(projects.uiKit.resources)
      implementation(projects.uiKit.messages)
    }

    iosMain.dependencies {
      implementation(libs.sqldelight.native)
    }
  }
}

android {
  namespace = "com.illiarb.peek.features.tasks"
}

sqldelight {
  databases {
    create("TasksDatabase") {
      packageName = "com.illiarb.peek.features.tasks"
      schemaOutputDirectory.set(File("src/commonMain/sqldelight/databases"))
    }
  }
}
