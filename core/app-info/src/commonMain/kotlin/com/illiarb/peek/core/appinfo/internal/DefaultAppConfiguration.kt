package com.illiarb.peek.core.appinfo.internal

import com.illiarb.peek.core.appinfo.AppConfiguration
import com.illiarb.peek.core.appinfo.DebugConfig
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.MemoryField
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart

@Inject
@SingleIn(AppScope::class)
internal class DefaultAppConfiguration(
  private val keyValueStorage: KeyValueStorage,
  private val memoryField: MemoryField<DebugConfig?> = MemoryField(value = null),
) : AppConfiguration {

  override val isAndroidQ: Boolean get() = isAndroidQ()

  override fun debugConfig(): Flow<DebugConfig> {
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
      .getOrThrow()
  }

  private fun defaultDebugConfig(): DebugConfig =
    DebugConfig(networkDelayEnabled = false)

  companion object {
    const val KEY_DEBUG_CONFIG = "KEY_DEBUG_CONFIG"
  }
}
