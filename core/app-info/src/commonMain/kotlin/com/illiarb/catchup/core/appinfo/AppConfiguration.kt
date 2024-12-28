package com.illiarb.catchup.core.appinfo

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DebugConfig(

  @SerialName("networkDelayEnabled")
  val networkDelayEnabled: Boolean,
)

public interface AppConfiguration {

  public val isAndroidQ: Boolean

  public fun debugConfig(): Flow<DebugConfig>

  public suspend fun updateDebugConfig(newConfig: DebugConfig): Result<Unit>

  public suspend fun resetDebugConfig(): Result<Unit>
}