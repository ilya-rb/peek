package com.illiarb.peek.gradle.plugins

import com.illiarb.peek.gradle.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplication : Plugin<Project> {

  override fun apply(target: Project) = with(target) {
    with(pluginManager) {
      apply("com.android.application")
      apply("org.gradle.android.cache-fix")
    }
    configureAndroid()
  }
}
