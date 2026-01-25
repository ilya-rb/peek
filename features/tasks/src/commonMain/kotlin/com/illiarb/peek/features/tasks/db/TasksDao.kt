package com.illiarb.peek.features.tasks.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.illiarb.peek.core.coroutines.CoroutineDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.core.data.ext.toEpochMilliseconds
import com.illiarb.peek.features.tasks.AllCompletions
import com.illiarb.peek.features.tasks.AllHabitsCreatedOnOrBefore
import com.illiarb.peek.features.tasks.OverdueTasksForDate
import com.illiarb.peek.features.tasks.TasksDatabase
import com.illiarb.peek.features.tasks.TasksForDateWithCompletion
import com.illiarb.peek.features.tasks.di.InternalApi
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.time.Clock

@Inject
internal class TasksDao(
  @InternalApi private val db: TasksDatabase,
  private val coroutineDispatchers: CoroutineDispatchers,
) {

  fun tasksForDate(date: LocalDate): Flow<List<TasksForDateWithCompletion>> {
    return db.tasksQueries
      .tasksForDateWithCompletion(date = date.toEpochMilliseconds())
      .asFlow()
      .mapToList(coroutineDispatchers.io)
  }

  fun overdueTasksForDate(
    date: LocalDate,
    maxLookbackDate: LocalDate,
  ): Flow<List<OverdueTasksForDate>> {
    return db.tasksQueries
      .overdueTasksForDate(
        date = date.toEpochMilliseconds(),
        maxLookbackDate = maxLookbackDate.toEpochMilliseconds(),
      )
      .asFlow()
      .mapToList(coroutineDispatchers.io)
  }

  suspend fun insertTask(task: Task): Result<Unit> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.insertTask(
          id = task.id,
          title = task.title,
          isHabit = if (task.habit) 1L else 0L,
          timeOfDay = task.timeOfDay.takeUnless { it == Anytime }?.name,
          createdAt = task.createdAt.toEpochMilliseconds(),
          createdForDate = task.createdForDate.toEpochMilliseconds(),
        ).await()

        Unit
      }
    }
  }

  suspend fun archiveTask(taskId: String): Result<Unit> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.archiveTask(taskId).await()
        Unit
      }
    }
  }

  suspend fun insertCompletion(taskId: String, date: LocalDate): Result<Unit> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.insertCompletion(
          taskId = taskId,
          date = date.toEpochMilliseconds(),
          completedAt = Clock.System.now().toEpochMilliseconds(),
        ).await()

        Unit
      }
    }
  }

  suspend fun deleteCompletion(taskId: String, date: LocalDate): Result<Unit> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.deleteCompletion(
          taskId = taskId,
          date = date.toEpochMilliseconds(),
        ).await()

        Unit
      }
    }
  }

  suspend fun getHabitsCreatedOnOrBefore(date: LocalDate): Result<List<AllHabitsCreatedOnOrBefore>> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.allHabitsCreatedOnOrBefore(date.toEpochMilliseconds()).executeAsList()
      }
    }
  }

  suspend fun getAllCompletions(): Result<List<AllCompletions>> {
    return withContext(coroutineDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.allCompletions().executeAsList()
      }
    }
  }
}
