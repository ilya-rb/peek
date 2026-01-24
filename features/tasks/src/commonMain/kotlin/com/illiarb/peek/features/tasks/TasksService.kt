package com.illiarb.peek.features.tasks

import com.illiarb.peek.core.coroutines.CoroutineDispatchers
import com.illiarb.peek.core.data.Async
import com.illiarb.peek.core.data.ext.asAsync
import com.illiarb.peek.core.data.ext.toLocalDate
import com.illiarb.peek.core.data.mapContent
import com.illiarb.peek.features.tasks.domain.HabitStatistics
import com.illiarb.peek.features.tasks.domain.StreakCalculator
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TaskDraft
import com.illiarb.peek.features.tasks.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.time.Clock

public interface TasksService {

  public fun getTasksForToday(): Flow<Async<List<Task>>>

  public fun getTasksFor(date: LocalDate): Flow<Async<List<Task>>>

  public fun getHabitsStatistics(): Flow<Async<HabitStatistics>>

  public suspend fun createTask(draft: TaskDraft): Result<Task>

  public suspend fun deleteTask(taskId: String): Result<Unit>

  public suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean>
}

internal class DefaultTasksService(
  private val repository: TasksRepository,
  private val streakCalculator: StreakCalculator,
  private val coroutineDispatchers: CoroutineDispatchers,
) : TasksService {

  override fun getTasksForToday(): Flow<Async<List<Task>>> {
    val today = Clock.System.now().toLocalDate()

    return combine(
      repository.getTasksFor(today),
      repository.getOverdueTasksFor(
        today,
        maxLookbackDate = today.minus(OVERDUE_TASKS_LOOKBACK_DAYS, DAY)
      ),
    ) { currentTasks, overdueTasks ->
      currentTasks.mergeWith(overdueTasks)
    }.mapContent { (current, overdue) ->
      (current + overdue).distinctBy { it.id }
    }
  }

  override fun getHabitsStatistics(): Flow<Async<HabitStatistics>> {
    return flow {
      val today = Clock.System.now().toLocalDate()
      val habits = repository.getHabitsCreatedBefore(today).getOrThrow()
      val completions = repository.getAllTasksCompletions().getOrThrow()
      val streak = streakCalculator.calculateCurrentStreak(today, habits, completions)

      emit(HabitStatistics(currentStreak = streak))
    }.asAsync().flowOn(coroutineDispatchers.default)
  }

  override fun getTasksFor(date: LocalDate): Flow<Async<List<Task>>> {
    return repository.getTasksFor(date)
  }

  override suspend fun createTask(draft: TaskDraft): Result<Task> {
    return repository.createTask(draft)
  }

  override suspend fun deleteTask(taskId: String): Result<Unit> {
    return repository.deleteTask(taskId)
  }

  override suspend fun toggleCompletion(task: Task, date: LocalDate): Result<Boolean> {
    return repository.toggleCompletion(task, date)
  }

  companion object {
    const val OVERDUE_TASKS_LOOKBACK_DAYS = 7
  }
}
