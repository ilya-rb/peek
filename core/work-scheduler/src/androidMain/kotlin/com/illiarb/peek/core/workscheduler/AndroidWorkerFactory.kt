package com.illiarb.peek.core.workscheduler

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.illiarb.peek.core.logging.Logger
import dev.zacsweers.metro.Provider

internal class AndroidWorkerFactory(
  private val workerProviders: Map<String, Provider<Worker>>,
) : WorkerFactory() {

  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters
  ): ListenableWorker {
    return object : CoroutineWorker(appContext, workerParameters) {
      override suspend fun doWork(): Result {
        val key = workerParameters.inputData.getString(KEY_WORKER_ID)
        val worker = workerProviders[key] ?: return Result.success().also {
          Logger.w { "Worker with $key not found, cannot schedule" }
        }

        val result = worker().doWork()
        return when (result) {
          is Worker.Result.Failure -> Result.Failure()
          is Worker.Result.Success -> Result.success()
        }
      }
    }
  }

  companion object {
    const val KEY_WORKER_ID = "KEY_WORKER_ID"
  }
}
