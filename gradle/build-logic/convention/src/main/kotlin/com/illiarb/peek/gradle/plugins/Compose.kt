package com.illiarb.peek.gradle.plugins

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
      //https://issuetracker.google.com/issues/338842143
      includeSourceInformation.set(true)

      stabilityConfigurationFiles.addAll(
        project.layout.projectDirectory.file("compose-stability.conf")
      )
    }
  }

  private fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
    extensions.configure<ComposeCompilerGradlePluginExtension>(block)
  }
}