package com.illiarb.peek.gradle.plugins

import com.android.build.gradle.BaseExtension
import com.illiarb.peek.gradle.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class Compose : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("org.jetbrains.compose")
      apply("org.jetbrains.kotlin.plugin.compose")
      apply("com.github.skydoves.compose.stability.analyzer")
    }

    composeCompiler {
      //https://issuetracker.google.com/issues/338842143
      includeSourceInformation.set(true)

      val configurationFile = rootProject.layout.projectDirectory.file("compose-stability.conf")
      if (!configurationFile.asFile.exists()) {
        throw IllegalArgumentException("Cannot find compose stability configuration file")
      }
      stabilityConfigurationFiles.add(configurationFile)
    }

    kotlin {
      sourceSets {
        commonMain.dependencies {
          implementation(libs.findLibrary("compose-runtime").get())
          implementation(libs.findLibrary("compose-ui").get())
          implementation(libs.findLibrary("compose-components-resources").get())
          implementation(libs.findLibrary("compose-material3").get())
          implementation(libs.findLibrary("compose-material3IconsExtended").get())
          implementation(libs.findLibrary("compose-preview").get())
        }
      }

      compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi")
      }
    }

    android {
      buildFeatures.compose = true

      dependencies {
        add("debugImplementation", libs.findLibrary("compose-uiTooling").get())
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
