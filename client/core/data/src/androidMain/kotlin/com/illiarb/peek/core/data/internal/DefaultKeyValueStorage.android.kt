package com.illiarb.peek.core.data.internal

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.illiarb.peek.core.data.KeyValueStorage
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

internal class DefaultKeyValueStorageFactory(
  private val context: Context,
  private val json: Json,
) : KeyValueStorage.Factory {

  override fun create(storageName: String): KeyValueStorage {
    val dataStore = PreferenceDataStoreFactory.createWithPath(
      produceFile = {
        context.filesDir.resolve("$storageName.preferences_pb").absolutePath.toPath()
      },
    )
    return DefaultKeyValueStorage(dataStore, json)
  }
}