package com.illiarb.catchup.core.appinfo.internal

import com.illiarb.catchup.core.appinfo.AppConfiguration
import com.illiarb.catchup.core.appinfo.AppEnvironment
import com.illiarb.catchup.core.appinfo.DebugConfig
import com.illiarb.catchup.core.data.KeyValueStorage
import com.illiarb.catchup.core.data.MemoryField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart

internal class DefaultAppConfiguration(
  private val environment: AppEnvironment,
  private val keyValueStorage: KeyValueStorage,
  private val memoryField: MemoryField<DebugConfig?> = MemoryField(value = null),
) : AppConfiguration {

  override fun debugConfig(): Flow<DebugConfig> {
    if (environment != AppEnvironment.DEV) {
      return emptyFlow()
    }

    return memoryField.observe()
      .onStart {
        if (memoryField.get() == null) {
          warmupDebugConfig()
        }
      }
      .mapNotNull { it }
  }

  override suspend fun updateDebugConfig(newConfig: DebugConfig): Result<Unit> {
    return keyValueStorage.put(KEY_DEBUG_CONFIG, newConfig, DebugConfig.serializer())
      .onSuccess { memoryField.set(newConfig) }
  }

  override suspend fun resetDebugConfig(): Result<Unit> {
    return updateDebugConfig(defaultDebugConfig())
  }

  private suspend fun warmupDebugConfig() {
    keyValueStorage.get(KEY_DEBUG_CONFIG, DebugConfig.serializer())
      .map { it ?: defaultDebugConfig() }
      .onSuccess(memoryField::set)
  }

  private fun defaultDebugConfig(): DebugConfig =
    DebugConfig(networkDelayEnabled = false)

  companion object {
    const val KEY_DEBUG_CONFIG = "KEY_DEBUG_CONFIG"
  }
}