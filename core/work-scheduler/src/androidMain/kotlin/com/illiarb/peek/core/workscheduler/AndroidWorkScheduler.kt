package com.illiarb.peek.core.workscheduler

import android.content.Context
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.illiarb.peek.core.arch.AndroidAppInitializer
import com.illiarb.peek.core.arch.AppInitializer
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.workscheduler.WorkConfiguration.PeriodicWorkConfiguration
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.toJavaDuration

@Inject
@ContributesIntoSet(AppScope::class, binding = binding<AppInitializer>())
@Suppress("unused")
public class AndroidWorkScheduler(
  private val workerProviders: Map<String, Provider<Worker>>,
) : WorkScheduler, AndroidAppInitializer {

  private val coroutineScope = CoroutineScope(SupervisorJob())

  override val key: String = "AndroidWorkScheduler"

  override fun initialise(context: Context) {
    WorkManager.initialize(
      context,
      Configuration.Builder()
        .setWorkerFactory(AndroidWorkerFactory(workerProviders))
        .build()
    )

    val startupWorkers = workerProviders.filter { it.value().startupWorker }
    if (startupWorkers.isEmpty()) {
      return
    }

    val workManager = WorkManager.getInstance(context)

    coroutineScope.launch {
      startupWorkers.forEach { provider ->
        when (val config = provider.value().config()) {
          is PeriodicWorkConfiguration -> {
            workManager.enqueueUniquePeriodicWork(
              uniqueWorkName = provider.key,
              existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
              request = config.asPeriodicWorkRequest(provider.key),
            )
          }
        }
      }
    }
  }

  private fun PeriodicWorkConfiguration.asPeriodicWorkRequest(workerKey: String): PeriodicWorkRequest {
    return PeriodicWorkRequestBuilder<CoroutineWorker>(interval.toJavaDuration())
      .setInputData(
        Data.Builder()
          .putString(AndroidWorkerFactory.KEY_WORKER_ID, workerKey)
          .build()
      )
      .setConstraints(
        Constraints.Builder()
          .setRequiresBatteryNotLow(batteryNotLowRequired)
          .let {
            if (connectivityRequired) {
              it.setRequiredNetworkType(NetworkType.CONNECTED)
            } else {
              it
            }
          }
          .build()
      )
      .build()
  }
}
