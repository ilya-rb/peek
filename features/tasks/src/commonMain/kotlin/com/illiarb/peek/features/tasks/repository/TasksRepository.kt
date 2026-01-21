package com.illiarb.peek.features.tasks.repository

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.ext.asAsync
import com.illiarb.peek.core.data.ext.toLocalDate
import com.illiarb.peek.core.data.ext.toLocalDateTime
import com.illiarb.peek.features.tasks.db.TasksDao
import com.illiarb.peek.features.tasks.db.asTask
import com.illiarb.peek.features.tasks.domain.HabitInfo
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskCompletion
import com.illiarb.peek.features.tasks.domain.TaskDraft
import com.illiarb.peek.features.tasks.domain.TaskNotCreatedException
import com.illiarb.peek.features.tasks.domain.TaskNotCreatedException.ErrorKind.NameTooLong
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Inject
@OptIn(ExperimentalUuidApi::class)
internal class TasksRepository(
  private val tasksDao: TasksDao,
) {

  fun getTasksFor(date: LocalDate): Flow<Async<List<Task>>> {
    return tasksDao.tasksForDate(date)
      .map { entries ->
        entries.map {
          it.asTask()
        }
      }
      .asAsync(withLoading = false)
  }

  fun getOverdueTasksFor(date: LocalDate, maxLookbackDate: LocalDate): Flow<Async<List<Task>>> {
    return tasksDao.overdueTasksForDate(date, maxLookbackDate)
      .map { entries ->
        entries.map {
          it.asTask()
        }
      }
      .asAsync(withLoading = false)
  }

  suspend fun getHabitsCreatedBefore(date: LocalDate): Result<List<HabitInfo>> {
    return tasksDao.getHabitsCreatedBefore(date).map { entries ->
      entries.map {
        HabitInfo(it.id, it.created_at.toLocalDate())
      }
    }
  }

  suspend fun getAllTasksCompletions(): Result<List<TaskCompletion>> {
    return tasksDao.getAllCompletions().map { entries ->
      entries.map {
        TaskCompletion(it.task_id, it.date.toLocalDate())
      }
    }
  }

  suspend fun createTask(draft: TaskDraft): Result<Task> {
    if (draft.title.length > Task.TASK_NAME_LIMIT) {
      return Result.failure(TaskNotCreatedException(NameTooLong))
    }

    val task = Task(
      id = Uuid.random().toString(),
      title = draft.title,
      habit = draft.habit,
      timeOfDay = draft.timeOfDay,
      createdAt = Clock.System.now().toLocalDateTime(),
      createdForDate = draft.forDate,
    )
    return tasksDao.insertTask(task).map { task }
  }

  suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean> {
    return if (task.completed) {
      tasksDao.deleteCompletion(task.id, date).map { false }
    } else {
      tasksDao.insertCompletion(task.id, date).map { true }
    }
  }

  suspend fun deleteTask(taskId: String): Result<Unit> {
    return tasksDao.archiveTask(taskId)
  }
}
