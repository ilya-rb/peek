rootProject.name = "peek"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  includeBuild("gradle/build-logic")

  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupAndSubgroups("androidx")
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
      }
    }
    mavenCentral()
  }
}

include(
  ":peek-api",
  ":app",
  ":core:coroutines",
  ":core:logging",
  ":core:network",
  ":core:app-info",
  ":core:arch",
  ":core:data",
  ":core:types",
  "features:home",
  "features:reader",
  "features:settings",
  ":ui-kit:core",
  ":ui-kit:resources",
  ":ui-kit:image-loader",
  ":summarizer",
)
