package com.illiarb.peek.core.data.di

import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides

public interface CoreDataComponent : CoreDataPlatformComponent {

  @Provides
  public fun provideJson(): Json = Json
}

public expect interface CoreDataPlatformComponent