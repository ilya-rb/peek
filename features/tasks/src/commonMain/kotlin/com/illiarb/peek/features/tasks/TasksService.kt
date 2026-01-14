package com.illiarb.peek.features.tasks

import com.illiarb.peek.core.data.Async
import com.illiarb.peek.features.tasks.domain.DayHistory
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

public interface TasksService {

  public fun getTasksForDate(date: LocalDate): Flow<Async<List<Task>>>

  public suspend fun addTask(draft: TaskDraft): Result<Task>

  public suspend fun deleteTask(taskId: String): Result<Unit>

  public suspend fun toggleCompletion(taskId: String, date: LocalDate): Result<Boolean>

  public fun getHabitHistory(startDate: LocalDate, endDate: LocalDate): Flow<Async<List<DayHistory>>>
}
