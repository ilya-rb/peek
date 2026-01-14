package com.illiarb.peek.features.tasks.repository

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.db.TasksDao
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Inject
internal class TasksRepository(
  private val tasksDao: TasksDao,
) {

  fun tasksForDate(date: LocalDate): Flow<Async<List<Task>>> {
    return tasksDao.tasksForDate(date)
      .map {
        Async.Content(it, contentRefreshing = false) as Async<List<Task>>
      }
      .catch {
        emit(Async.Error(it))
      }
  }

  @OptIn(ExperimentalUuidApi::class)
  suspend fun addTask(draft: TaskDraft): Result<Task> {
    val task = Task(
      id = Uuid.random().toString(),
      title = draft.title,
      habit = draft.habit,
      timeOfDay = draft.timeOfDay,
      createdAt = Clock.System.now(),
      createdForDate = draft.forDate,
    )
    return tasksDao.insertTask(task).map { task }
  }

  suspend fun deleteTask(taskId: String): Result<Unit> {
    return tasksDao.archiveTask(taskId)
  }

  suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean> {
    return if (task.completed) {
      tasksDao.deleteCompletion(task.id, date).map { false }
    } else {
      tasksDao.insertCompletion(task.id, date).map { true }
    }
  }
}
