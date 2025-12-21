package com.illiarb.peek.core.data.di

import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json

@BindingContainer(includes = [CoreDataPlatformBindings::class])
public object CoreDataBindings {

  @Provides
  public fun provideJson(): Json = Json
}

@BindingContainer
public expect abstract class CoreDataPlatformBindings