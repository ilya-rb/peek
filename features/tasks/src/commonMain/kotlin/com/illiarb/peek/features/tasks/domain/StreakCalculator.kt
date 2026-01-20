package com.illiarb.peek.features.tasks.domain

import dev.zacsweers.metro.Inject
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

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
    val timezone = TimeZone.currentSystemDefault()

    val earliestHabitDate = habits.minOf { habit ->
      habit.createdAt.toLocalDateTime(timezone).date
    }

    val habitsForToday = habits.filter {
      it.createdAt.toLocalDateTime(timezone).date <= today
    }
    val todayCompleted = if (habitsForToday.isNotEmpty()) {
      val completedIds = completionsByDate[today].orEmpty().map { it.taskId }.toSet()
      habitsForToday.all { it.id in completedIds }
    } else {
      false
    }
    var currentDate = if (todayCompleted) {
      today
    } else {
      today.minus(value = 1, DateTimeUnit.DAY)
    }

    while (currentDate >= earliestHabitDate) {
      val habitsForDate = habits.filter {
        it.createdAt.toLocalDateTime(timezone).date <= currentDate
      }

      if (habitsForDate.isNotEmpty()) {
        val completedIds = completionsByDate[currentDate].orEmpty().map { it.taskId }.toSet()

        val allCompleted = habitsForDate.all { it.id in completedIds }
        if (!allCompleted) {
          // Streak broken
          break
        }

        streak++
      }

      currentDate = currentDate.minus(1, DateTimeUnit.DAY)
    }

    return streak
  }
}
