package com.illiarb.peek.core.data.internal

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.illiarb.peek.core.data.KeyValueStorage
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal class DefaultKeyValueStorageFactory(
  private val json: Json,
) : KeyValueStorage.Factory {

  @OptIn(ExperimentalForeignApi::class)
  override fun create(storageName: String): KeyValueStorage {
    val dataStore = PreferenceDataStoreFactory.createWithPath(
      produceFile = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
          directory = NSDocumentDirectory,
          inDomain = NSUserDomainMask,
          appropriateForURL = null,
          create = false,
          error = null,
        )
        (requireNotNull(documentDirectory).path + "/$storageName").toPath()
      }
    )
    return DefaultKeyValueStorage(dataStore, json)
  }
}
