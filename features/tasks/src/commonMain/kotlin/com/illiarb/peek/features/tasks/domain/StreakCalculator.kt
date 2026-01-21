package com.illiarb.peek.features.tasks.domain

import dev.zacsweers.metro.Inject
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

@Inject
internal class StreakCalculator {

  fun calculateCurrentStreak(
    today: LocalDate,
    habits: List<HabitInfo>,
    completions: List<TaskCompletion>,
  ): Int {
    if (habits.isEmpty()) {
      return 0
    }

    var streak = 0

    val completionsByDate = completions.groupBy { it.date }
    val earliestHabitDate = habits.minOf { habit -> habit.createdAt }

    val habitsForToday = habits.filter { it.createdAt <= today }
    val todayCompleted = habitsCompletedFor(today, habitsForToday, completionsByDate)

    var currentDate = if (todayCompleted) {
      today
    } else {
      today.minus(1, DAY)
    }

    while (currentDate >= earliestHabitDate) {
      val habitsForDate = habits.filter { it.createdAt <= currentDate }
      if (habitsForDate.isNotEmpty()) {
        val completed = habitsCompletedFor(currentDate, habitsForDate, completionsByDate)
        if (completed.not()) {
          // Streak broken
          break
        }
        streak++
      }
      currentDate = currentDate.minus(1, DAY)
    }

    return streak
  }

  private fun habitsCompletedFor(
    date: LocalDate,
    habits: List<HabitInfo>,
    completions: Map<LocalDate, List<TaskCompletion>>
  ): Boolean {
    if (habits.isEmpty()) {
      return false
    }
    val completedIds = completions[date].orEmpty().map { it.taskId }.toSet()
    return habits.all { it.id in completedIds }
  }
}
