package com.illiarb.peek.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.getCompileSdk: Int
  get() = libs.findVersion("android-compileSdk").get().displayName.toInt()

internal val Project.getTargetSdk: Int
  get() = libs.findVersion("android-targetSdk").get().displayName.toInt()

internal val Project.getMinSdk: Int
  get() = libs.findVersion("android-minSdk").get().displayName.toInt()
