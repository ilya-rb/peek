package com.illiarb.catchup.gradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.configureAndroid() {
  android {
    compileSdkVersion(Versions.COMPILE_SDK)

    defaultConfig {
      minSdk = Versions.MIN_SDK
      targetSdk = Versions.TARGET_SDK
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
