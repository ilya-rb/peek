package com.illiarb.peek.gradle.plugins

import com.illiarb.peek.gradle.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinOnlyTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

class KotlinMultiplatform : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("org.jetbrains.kotlin.multiplatform")
    }

    kotlinMultiplatform {
      applyDefaultHierarchyTemplate()

      androidTarget()
      iosArm64()
      iosSimulatorArm64()

      targets.configureEach {
        val androidJvm = platformType == KotlinPlatformType.androidJvm

        compilations.configureEach {
          compileTaskProvider.configure {
            compilerOptions {
              if (androidJvm) {
                freeCompilerArgs.addAll(
                  "-P",
                  "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.illiarb.peek.core.arch.CommonParcelize",
                )
              }
            }
          }
        }
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

      configureKotlin()
    }
  }

  private fun Project.kotlinMultiplatform(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension>(action)
  }
}