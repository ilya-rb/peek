package com.illiarb.peek.core.data.di

import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.internal.DefaultKeyValueStorageFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
public actual abstract class CoreDataPlatformBindings {

  @Binds
  internal abstract val DefaultKeyValueStorageFactory.bind: KeyValueStorage.Factory
}
