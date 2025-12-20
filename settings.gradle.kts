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
  ":app",
  ":core:app-info",
  ":core:arch",
  ":core:coroutines",
  ":core:data",
  ":core:logging",
  ":core:network",
  ":core:types",
  ":peek-api",
  ":ui-kit:core",
  ":ui-kit:image-loader",
  ":ui-kit:resources",
  "features:home",
  "features:reader",
  "features:settings",
  "features:summarizer",
)
