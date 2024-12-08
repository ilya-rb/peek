package com.illiarb.catchup.gradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

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
  }

  dependencies {
    "coreLibraryDesugaring"(libs.findLibrary("tools.desugarJdkLibs").get())
  }
}

private fun Project.android(action: BaseExtension.() -> Unit) {
  extensions.configure<BaseExtension>(action)
}
