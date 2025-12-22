package com.illiarb.peek.gradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

fun Project.configureAndroid() {
  android {
    compileSdkVersion(getCompileSdk)

    defaultConfig {
      minSdk = getMinSdk
      targetSdk = getTargetSdk
    }

    compileOptions {
      isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures.buildConfig = false
  }

  tasks.withType<Test> {
    testLogging {
      events(
        TestLogEvent.PASSED,
        TestLogEvent.FAILED,
        TestLogEvent.SKIPPED,
        TestLogEvent.STANDARD_OUT,
        TestLogEvent.STANDARD_ERROR,
      )
      exceptionFormat = TestExceptionFormat.FULL
      showCauses = true
      showExceptions = true
      showStackTraces = true
    }
  }

  dependencies {
    "coreLibraryDesugaring"(libs.findLibrary("tools.desugarJdkLibs").get())
  }
}

private fun Project.android(action: BaseExtension.() -> Unit) {
  extensions.configure<BaseExtension>(action)
}
