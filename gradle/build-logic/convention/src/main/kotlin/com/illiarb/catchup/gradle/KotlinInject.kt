package com.illiarb.catchup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

class KotlinInject : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    if (!pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
      return
    }

    with(pluginManager) {
      apply("com.google.devtools.ksp")
    }

    kotlinMultiplatform {
      sourceSets.commonMain.dependencies {
        implementation(libs.findLibrary("kotlin.inject.runtime").get())
      }
      
      metadata {
        compilations.configureEach {
          if (name == KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME) {
            compileTaskProvider.configure {
              // We replace the default library names with something more unique (the project path).
              // This allows us to avoid the annoying issue of `duplicate library name: foo_commonMain`
              // https://youtrack.jetbrains.com/issue/KT-57914
              val projectPath = this@with.path.substring(1).replace(":", "_")
              this as KotlinCompileCommon
              moduleName.set("${projectPath}_commonMain")
            }
          }
        }
      }
      
      addKspToTargets(extension = this, configurationNameSuffix = "")
    }
  }

  private fun Project.addKspToTargets(
    extension: KotlinMultiplatformExtension,
    configurationNameSuffix: String,
  ) {
    val compiler = libs.findLibrary("kotlin.inject.compiler").get()

    dependencies {
      extension.targets
        .asSequence()
        .filter { target -> target.platformType != KotlinPlatformType.common }
        .forEach { target ->
          add(
            "ksp${target.targetName.capitalized()}$configurationNameSuffix",
            compiler,
          )
        }
    }
  }
  
  private fun Project.kotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(action)
  }
}
