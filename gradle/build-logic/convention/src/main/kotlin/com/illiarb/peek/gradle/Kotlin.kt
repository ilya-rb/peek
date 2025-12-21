package com.illiarb.peek.gradle

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
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

  configureDetekt()
}

private fun Project.configureDetekt() {
  pluginManager.apply("io.gitlab.arturbosch.detekt")

  detekt {
    toolVersion = libs.findVersion("detekt").get().displayName
    config.setFrom(rootProject.file("detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom(
      "src/commonMain/kotlin",
      "src/androidMain/kotlin",
    )
  }

  tasks.withType(Detekt::class.java) {
    reports {
      xml.required.set(false)

      val projectName = this@configureDetekt.name.lowercase()
      html.required.set(true)
      html.outputLocation.set(rootProject.file("build/reports/detekt_$projectName.html"))
    }

    include("**/*.kt")
    include("**/*.kts")
    exclude("build/")
  }
}

private fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
  extensions.configure<KotlinMultiplatformExtension>(action)
}

private fun Project.detekt(action: DetektExtension.() -> Unit) {
  extensions.configure<DetektExtension>(action)
}
