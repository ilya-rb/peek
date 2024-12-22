package com.illiarb.catchup.features.settings.data

import com.illiarb.catchup.core.data.MemoryField
import kotlinx.coroutines.flow.Flow

public interface SettingsService {

  public fun observeSettingChange(type: SettingType): Flow<Boolean>

  public fun updateSetting(type: SettingType, value: Boolean)

  public enum class SettingType {
    DYNAMIC_COLORS
  }
}

internal class DefaultSettingsService : SettingsService {

  private val settings = mapOf(
    SettingsService.SettingType.DYNAMIC_COLORS to MemoryField(value = false)
  )

  override fun observeSettingChange(type: SettingsService.SettingType): Flow<Boolean> {
    return requireNotNull(settings[type]).observe()
  }

  override fun updateSetting(type: SettingsService.SettingType, value: Boolean) {
    requireNotNull(settings[type]).set(value)
  }
}