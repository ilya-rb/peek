package com.illiarb.peek.features.settings.data

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.KeyValueStorage
import com.illiarb.peek.features.settings.data.SettingsService.Settings
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public interface SettingsService {

  public fun observeSettings(): Flow<Settings>

  public suspend fun updateSettings(settings: Settings): Result<Unit>

  public suspend fun getSettings(): Result<Settings>

  @Serializable
  public data class Settings(
    @SerialName("darkTheme") val darkTheme: Boolean,
    @SerialName("dynamicColors") val dynamicColors: Boolean,
    @SerialName("articlesRetentionDays") val articlesRetentionDays: Int,
  ) {

    val articlesRetentionDaysOptions: List<Int>
      get() = RETENTION_DAYS_OPTIONS

    public companion object {
      private val RETENTION_DAYS_OPTIONS: List<Int> = listOf(7, 10, 14)

      public fun defaults(): Settings {
        return Settings(
          darkTheme = true,
          dynamicColors = false,
          articlesRetentionDays = 10,
        )
      }
    }
  }
}

@Inject
@SingleIn(AppScope::class)
internal class DefaultSettingsService(
  private val keyValueStorage: KeyValueStorage,
) : SettingsService {

  override fun observeSettings(): Flow<Settings> {
    return keyValueStorage.observe(KEY_SETTINGS, Settings.serializer()).onStart {
      // Warmup storage if needed
      getSettingsFromStorage()
    }
  }

  override suspend fun getSettings(): Result<Settings> {
    return getSettingsFromStorage()
  }

  override suspend fun updateSettings(settings: Settings): Result<Unit> {
    return keyValueStorage.put(KEY_SETTINGS, settings, Settings.serializer())
  }

  private suspend fun getSettingsFromStorage(): Result<Settings> {
    return keyValueStorage.get(KEY_SETTINGS, Settings.serializer()).onSuccess { cached ->
      if (cached == null) {
        updateSettings(Settings.defaults())
      }
    }.map {
      it ?: Settings.defaults()
    }
  }

  companion object {
    const val KEY_SETTINGS = "KEY_SETTINGS"
  }
}
