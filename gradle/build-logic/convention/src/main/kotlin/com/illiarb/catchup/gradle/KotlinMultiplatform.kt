package com.illiarb.catchup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

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
              freeCompilerArgs.add("-Xexpect-actual-classes")

              if (androidJvm) {
                freeCompilerArgs.addAll(
                  "-P",
                  "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.illiarb.catchup.core.arch.CommonParcelize"
                )
              }
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
