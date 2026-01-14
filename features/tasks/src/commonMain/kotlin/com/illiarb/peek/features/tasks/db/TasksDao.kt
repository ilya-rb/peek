package com.illiarb.peek.features.tasks.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.illiarb.peek.core.coroutines.AppDispatchers
import com.illiarb.peek.core.coroutines.suspendRunCatching
import com.illiarb.peek.features.tasks.TasksDatabase
import com.illiarb.peek.features.tasks.TasksForDateWithCompletion
import com.illiarb.peek.features.tasks.di.InternalApi
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.Instant

@Inject
internal class TasksDao(
  private val appDispatchers: AppDispatchers,
  @InternalApi private val db: TasksDatabase,
) {

  fun tasksForDate(date: LocalDate): Flow<List<Task>> {
    return db.tasksQueries
      .tasksForDateWithCompletion(date.toString())
      .asFlow()
      .mapToList(appDispatchers.io)
      .map { tasks ->
        tasks.map { task ->
          task.toDomain()
        }
      }
  }

  suspend fun insertTask(task: Task): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.insertTask(
          id = task.id,
          title = task.title,
          isHabit = if (task.habit) 1L else 0L,
          timeOfDay = task.timeOfDay?.name,
          createdAt = task.createdAt.toEpochMilliseconds(),
          createdForDate = task.createdForDate?.toString(),
        ).await()
        Unit
      }
    }
  }

  suspend fun archiveTask(taskId: String): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.archiveTask(taskId).await()
        Unit
      }
    }
  }

  suspend fun insertCompletion(taskId: String, date: LocalDate): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.insertCompletion(
          taskId = taskId,
          date = date.toString(),
          completedAt = Clock.System.now().toEpochMilliseconds(),
        ).await()
        Unit
      }
    }
  }

  suspend fun deleteCompletion(taskId: String, date: LocalDate): Result<Unit> {
    return withContext(appDispatchers.io) {
      suspendRunCatching {
        db.tasksQueries.deleteCompletion(
          taskId = taskId,
          date = date.toString(),
        ).await()
        Unit
      }
    }
  }

  private fun TasksForDateWithCompletion.toDomain(): Task {
    return Task(
      id = id,
      title = title,
      habit = is_habit == 1L,
      timeOfDay = TimeOfDay.fromString(time_of_day),
      createdAt = Instant.fromEpochMilliseconds(created_at),
      createdForDate = created_for_date?.let { LocalDate.parse(it) },
      archived = archived == 1L,
      completed = is_completed == 1L,
    )
  }
}
