package com.illiarb.peek.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.configureKotlin() {
  configureJava()

  kotlin {
    explicitApi = ExplicitApiMode.Strict

    compilerOptions {
      freeCompilerArgs.add("-Xcontext-receivers")
      freeCompilerArgs.add("-Xexpect-actual-classes")

      optIn.add("kotlin.time.ExperimentalTime")
    }
  }
}

private fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}
