package com.illiarb.peek.core.data.internal

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.data.KeyValueStorage
import kotlinx.coroutines.flow.first
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class DefaultKeyValueStorage(
  private val dataStore: DataStore<Preferences>,
  private val json: Json,
) : KeyValueStorage {

  override suspend fun <T> put(key: String, value: T, serializer: KSerializer<T>): Result<Unit> {
    return suspendRunCatching {
      dataStore.edit { preferences ->
        preferences += stringPreferencesKey(key) to json.encodeToString(serializer, value)
      }
    }
  }

  override suspend fun <T> get(key: String, serializer: KSerializer<T>): Result<T?> {
    return suspendRunCatching {
      val preferences = dataStore.data.first()
      val value = preferences[stringPreferencesKey(key)]

      if (value.isNullOrEmpty()) {
        null
      } else {
        json.decodeFromString(serializer, value)
      }
    }
  }
}