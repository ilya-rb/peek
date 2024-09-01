package com.illiarb.catchup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class Compose : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("org.jetbrains.compose")
      apply("org.jetbrains.kotlin.plugin.compose")
    }
    configureCompose()
  }

  private fun Project.configureCompose() {
    composeCompiler {
      // https://medium.com/androiddevelopers/jetpack-compose-strong-skipping-mode-explained-cbdb2aa4b900
      enableStrongSkippingMode.set(true)
      //https://issuetracker.google.com/issues/338842143
      includeSourceInformation.set(true)

      stabilityConfigurationFile.set(rootProject.file("compose-stability.conf"))
    }
  }

  private fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure<ComposeCompilerGradlePluginExtension>(block)
  }
}
