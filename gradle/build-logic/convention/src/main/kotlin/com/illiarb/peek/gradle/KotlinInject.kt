package com.illiarb.peek.gradle

import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

fun Project.addKspDependencyForAllTargets(dependencyNotation: Any) =
  addKspDependencyForAllTargets("", dependencyNotation)

fun Project.addKspTestDependencyForAllTargets(dependencyNotation: Any) =
  addKspDependencyForAllTargets("Test", dependencyNotation)

private fun Project.addKspDependencyForAllTargets(
  configurationNameSuffix: String,
  dependencyNotation: Any,
) {
  val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()

  dependencies {
    kmpExtension.targets
      .asSequence()
      .filter { target ->
        // Don't add KSP for common target, only final platforms
        target.platformType != KotlinPlatformType.common
      }
      .forEach { target ->
        add(
          "ksp${target.targetName.capitalized()}$configurationNameSuffix",
          dependencyNotation,
        )
      }
  }
}
