plugins {
  `kotlin-dsl`
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

dependencies {
  compileOnly(libs.gradlePlugin.android)
  compileOnly(libs.gradlePlugin.kotlin)
  compileOnly(libs.gradlePlugin.compose)
  compileOnly(libs.gradlePlugin.composeCompiler)
}

gradlePlugin {
  plugins {
    register("kotlinMultiplatform") {
      id = "com.illiarb.catchup.kotlin.multiplatform"
      implementationClass = "com.illiarb.catchup.gradle.KotlinMultiplatform"
    }

    register("androidApplication") {
      id = "com.illiarb.catchup.android.application"
      implementationClass = "com.illiarb.catchup.gradle.AndroidApplication"
    }

    register("androidLibrary") {
      id = "com.illiarb.catchup.android.library"
      implementationClass = "com.illiarb.catchup.gradle.AndroidLibrary"
    }

    register("compose") {
      id = "com.illiarb.catchup.compose"
      implementationClass = "com.illiarb.catchup.gradle.Compose"
    }
  }
}
