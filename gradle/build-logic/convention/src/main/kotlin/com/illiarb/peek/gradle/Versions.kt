package com.illiarb.peek.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

internal val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal val Project.getCompileSdk: Int
  get() = libs.findVersion("android-compileSdk").get().displayName.toInt()

internal val Project.getTargetSdk: Int
  get() = libs.findVersion("android-targetSdk").get().displayName.toInt()

internal val Project.getMinSdk: Int
  get() = libs.findVersion("android-minSdk").get().displayName.toInt()

fun Project.getLocalProperty(key: String): String? {
  val propertiesFile = project.rootProject.file("local.properties")

  return when {
    propertiesFile.exists() -> {
      val properties = Properties()
      propertiesFile.inputStream().buffered().use { input ->
        properties.load(input)
      }
      properties.getProperty(key)
    }

    isCI() -> ""

    else -> null.also {
      propertiesFile.createNewFile()
    }
  }
}

private fun Project.isCI(): Boolean {
  val ciEnv = System.getenv("CI")
  if (ciEnv != null && ciEnv.equals("true", ignoreCase = true)) {
    return true
  }

  val ciProperty = findProperty("ci") as? String
  if (ciProperty != null && ciProperty.equals("true", ignoreCase = true)) {
    return true
  }

  val ciLocalProperty = getLocalProperty("ci")
  return ciLocalProperty != null && ciLocalProperty.equals("true", ignoreCase = true)
}
