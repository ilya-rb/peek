package com.illiarb.peek.gradle.plugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class Compose : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("org.jetbrains.compose")
      apply("org.jetbrains.kotlin.plugin.compose")
      apply("com.github.skydoves.compose.stability.analyzer")
    }

    composeCompiler {
      enableStrongSkippingMode.set(true)
      //https://issuetracker.google.com/issues/338842143
      includeSourceInformation.set(true)

      val configurationFile = rootProject.layout.projectDirectory.file("compose-stability.conf")
      if (!configurationFile.asFile.exists()) {
        throw IllegalArgumentException("Cannot find compose stability configuration file")
      }
      stabilityConfigurationFile.set(configurationFile)
    }

    val composeDependencies = ComposePlugin.Dependencies(target)

    kotlin {
      sourceSets {
        androidMain.dependencies {
          implementation(composeDependencies.preview)
        }

        commonMain.dependencies {
          implementation(composeDependencies.runtime)
          implementation(composeDependencies.foundation)
          implementation(composeDependencies.ui)
          implementation(composeDependencies.components.resources)
          implementation(composeDependencies.material3)
          implementation(composeDependencies.materialIconsExtended)
        }
      }
    }

    android {
      buildFeatures.compose = true

      dependencies {
        add("debugImplementation", composeDependencies.uiTooling)
      }
    }
  }

  private fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure<ComposeCompilerGradlePluginExtension>(block)
  }

  private fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(action)
  }

  private fun Project.android(action: BaseExtension.() -> Unit) {
    extensions.configure<BaseExtension>(action)
  }
}