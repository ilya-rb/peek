package com.illiarb.peek.core.data.di

import android.content.Context
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.internal.DefaultKeyValueStorageFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json

@BindingContainer
public actual object CoreDataPlatformBindings {

  @Provides
  @SingleIn(AppScope::class)
  public fun provideKeyStorageFactory(context: Context, json: Json): KeyValueStorage.Factory {
    return DefaultKeyValueStorageFactory(context, json)
  }
}