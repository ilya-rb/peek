package com.illiarb.catchup.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

object Versions {
  const val COMPILE_SDK = 34
  const val MIN_SDK = 24
  const val TARGET_SDK = 34
}

internal val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
