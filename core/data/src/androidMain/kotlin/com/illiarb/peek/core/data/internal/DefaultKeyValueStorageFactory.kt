package com.illiarb.peek.core.data.internal

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.ConcurrentHashMapCache
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.MemoryCache
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

@Inject
@SingleIn(AppScope::class)
internal class DefaultKeyValueStorageFactory(
  private val context: Context,
  private val json: Json,
  private val dataStoreCache: MemoryCache<String> = ConcurrentHashMapCache(),
) : KeyValueStorage.Factory {

  override fun create(storageName: String): KeyValueStorage {
    val dataStore = dataStoreCache.getOrCreate(
      key = storageName,
      creator = {
        PreferenceDataStoreFactory.createWithPath(
          produceFile = {
            context.filesDir.resolve("$storageName.preferences_pb").absolutePath.toPath()
          },
        )
      },
    )
    return DefaultKeyValueStorage(dataStore, json)
  }
}
