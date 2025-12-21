package com.illiarb.peek.features.settings.data

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.core.data.MemoryField
import com.illiarb.peek.features.settings.data.SettingsService.SettingType
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.builtins.serializer

public interface SettingsService {

  public fun observeSettingChange(type: SettingType): Flow<Boolean>

  public suspend fun updateSetting(type: SettingType, value: Boolean): Result<Unit>

  public enum class SettingType(public val defaultValue: Boolean) {
    DYNAMIC_COLORS(true),
    DARK_THEME(true),
  }
}

@Inject
@SingleIn(AppScope::class)
internal class DefaultSettingsService(
  private val keyValueStorage: KeyValueStorage,
  private val cachedSettings: MutableMap<SettingType, MemoryField<Boolean>> = mutableMapOf(),
) : SettingsService {

  override fun observeSettingChange(type: SettingType): Flow<Boolean> =
    flow {
      val fromMemory = cachedSettings[type]
      if (fromMemory == null) {
        val memoryField = MemoryField(type.defaultValue)
        cachedSettings[type] = memoryField

        keyValueStorage.get(type.name, Boolean.serializer()).onSuccess { fromStorage ->
          if (fromStorage != null) {
            memoryField.set(fromStorage)
          }
        }
        emitAll(memoryField.observe())
      } else {
        emitAll(fromMemory.observe())
      }
    }

  override suspend fun updateSetting(type: SettingType, value: Boolean): Result<Unit> {
    return keyValueStorage.put(type.name, value, Boolean.serializer())
      .onSuccess { cachedSettings[type]?.set(value) }
  }
}
