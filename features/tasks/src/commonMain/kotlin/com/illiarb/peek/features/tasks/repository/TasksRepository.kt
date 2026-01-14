package com.illiarb.peek.features.tasks.repository

import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.MemoryCache
import com.illiarb.peek.features.tasks.db.TasksDao
import com.illiarb.peek.features.tasks.di.InternalApi
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
internal class TasksRepository(
  private val tasksDao: TasksDao,
  @InternalApi private val memoryCache: MemoryCache<String>,
) {

  fun tasksForDate(date: LocalDate): Flow<Async<List<Task>>> {
    return tasksDao.tasksForDate(date).onEach { tasks ->
      tasks.forEach { task ->
        memoryCache.put(task.id, task)
      }
    }.map {
      Async.Content(it, contentRefreshing = false) as Async<List<Task>>
    }.catch {
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
    return tasksDao.archiveTask(taskId).onSuccess {
      memoryCache.delete(taskId)
    }
  }

  suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean> {
    val result = if (task.completed) {
      tasksDao.deleteCompletion(task.id, date).map { false }
    } else {
      tasksDao.insertCompletion(task.id, date).map { true }
    }
    return result.onSuccess { completed ->
      memoryCache.put(task.id, task.copy(completed = completed))
    }
  }
}
