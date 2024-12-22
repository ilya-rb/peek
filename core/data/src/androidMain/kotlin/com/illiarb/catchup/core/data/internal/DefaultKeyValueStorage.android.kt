package com.illiarb.catchup.core.data.internal

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.illiarb.catchup.core.data.KeyValueStorage
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

internal class DefaultKeyValueStorageFactory(
  private val application: Application,
  private val json: Json,
) : KeyValueStorage.Factory {

  override fun create(storageName: String): KeyValueStorage {
    val dataStore = PreferenceDataStoreFactory.createWithPath(
      produceFile = {
        application.filesDir.resolve("$storageName.preferences_pb").absolutePath.toPath()
      },
    )
    return DefaultKeyValueStorage(dataStore, json)
  }
}