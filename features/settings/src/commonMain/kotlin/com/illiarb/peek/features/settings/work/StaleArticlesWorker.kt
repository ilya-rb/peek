package com.illiarb.peek.features.settings.work

import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.logging.Logger
import com.illiarb.peek.core.workscheduler.WorkConfiguration
import com.illiarb.peek.core.workscheduler.Worker
import com.illiarb.peek.core.workscheduler.WorkerKey
import com.illiarb.peek.features.settings.data.SettingsService
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlin.time.Duration.Companion.days

@Inject
@WorkerKey("StaleArticlesWorker")
@ContributesIntoMap(AppScope::class, binding = binding<Worker>())
public class StaleArticlesWorker(
  private val peekApiService: PeekApiService,
  private val settingsService: SettingsService,
) : Worker {

  override val key: String = "StaleArticlesWorker"

  override val startupWorker: Boolean = true

  override suspend fun doWork(): Worker.Result {
    val settings = settingsService.getSettings().getOrElse { error ->
      Logger.e(throwable = error)
      return Worker.Result.Failure
    }

    return peekApiService.deleteArticlesOlderThan(settings.articlesRetentionDays.days).fold(
      onSuccess = {
        Worker.Result.Success
      },
      onFailure = {
        Worker.Result.Failure
      },
    )
  }

  override suspend fun config(): WorkConfiguration {
    return WorkConfiguration.PeriodicWorkConfiguration(1.days)
  }
}
