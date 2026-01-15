package com.illiarb.peek.features.tasks

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.DayHistory
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import com.illiarb.peek.features.tasks.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

internal interface TasksService {

  fun getTasksForDate(date: LocalDate): Flow<Async<List<Task>>>

  suspend fun addTask(draft: TaskDraft): Result<Task>

  suspend fun deleteTask(taskId: String): Result<Unit>

  suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean>

  fun getHabitHistory(
    startDate: LocalDate,
    endDate: LocalDate
  ): Flow<Async<List<DayHistory>>>
}

internal class DefaultTasksService(
  private val repository: TasksRepository,
) : TasksService {

  override fun getTasksForDate(date: LocalDate): Flow<Async<List<Task>>> {
    return repository.tasksForDate(date)
  }

  override suspend fun addTask(draft: TaskDraft): Result<Task> {
    return repository.addTask(draft)
  }

  override suspend fun deleteTask(taskId: String): Result<Unit> {
    return repository.deleteTask(taskId)
  }

  override suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean> {
    return repository.toggleCompletion(task, date)
  }

  override fun getHabitHistory(
    startDate: LocalDate,
    endDate: LocalDate,
  ): Flow<Async<List<DayHistory>>> {
    throw NotImplementedError("Habit history not implemented yet")
  }
}
