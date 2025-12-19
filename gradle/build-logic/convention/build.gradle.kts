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
      id = "com.illiarb.peek.kotlin.multiplatform"
      implementationClass = "com.illiarb.peek.gradle.KotlinMultiplatform"
    }

    register("androidApplication") {
      id = "com.illiarb.peek.android.application"
      implementationClass = "com.illiarb.peek.gradle.AndroidApplication"
    }

    register("androidLibrary") {
      id = "com.illiarb.peek.android.library"
      implementationClass = "com.illiarb.peek.gradle.AndroidLibrary"
    }

    register("compose") {
      id = "com.illiarb.peek.compose"
      implementationClass = "com.illiarb.peek.gradle.Compose"
    }
  }
}
