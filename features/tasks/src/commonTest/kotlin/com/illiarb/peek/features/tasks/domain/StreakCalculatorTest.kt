package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StreakCalculatorTest {

  private val streakCalculator = StreakCalculator()

  @Test
  fun `it should return zero streak when no habits exist`() {
    val today = LocalDate(2026, 1, 15)
    val habits = emptyList<HabitInfo>()
    val completions = emptyList<TaskCompletion>()

    val streak = streakCalculator.calculateCurrentStreak(
      habits = habits,
      completions = completions,
      today = today,
    )

    assertEquals(0, streak)
  }

  @Test
  fun `it should return 1 when all habits completed only today`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(1, streak)
  }

  @Test
  fun `it should show yesterday's streak when today incomplete`() {
    val today = LocalDate(2026, 1, 15)
    val habit1 = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val habit2 = HabitInfo(
      id = "h2",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      // Today: only h1 complete, h2 missing
      TaskCompletion("h1", today),
      // Yesterday: both complete
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h2", LocalDate(2026, 1, 14)),
      // Day before: both complete
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
      TaskCompletion("h2", LocalDate(2026, 1, 13)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit1, habit2),
      completions = completions,
      today = today,
    )

    assertEquals(2, streak)
  }

  @Test
  fun `it should return 0 when both today and yesterday are incomplete`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
      TaskCompletion("h1", LocalDate(2026, 1, 12)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(0, streak)
  }

  @Test
  fun `it should include today when today is complete`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(3, streak)
  }

  @Test
  fun `it should count consecutive days backwards correctly`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
      TaskCompletion("h1", LocalDate(2026, 1, 12)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(4, streak)
  }

  @Test
  fun `it should break on first incomplete day`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
      // Missing day 12 - streak should break
      TaskCompletion("h1", LocalDate(2026, 1, 11)),
      TaskCompletion("h1", LocalDate(2026, 1, 10)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(3, streak)
  }

  @Test
  fun `it should ignore days before habit creation`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 13),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(3, streak)
  }

  @Test
  fun `it should require all habits to be complete for streak to count`() {
    val today = LocalDate(2026, 1, 15)
    val habit1 = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val habit2 = HabitInfo(
      id = "h2",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h2", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h2", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit1, habit2),
      completions = completions,
      today = today,
    )

    assertEquals(2, streak)
  }

  @Test
  fun `it should handle multiple habits with different creation dates`() {
    val today = LocalDate(2026, 1, 15)
    val habit1 = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val habit2 = HabitInfo(
      id = "h2",
      createdAt = LocalDate(2026, 1, 13),
    )
    val completions = listOf(
      TaskCompletion("h1", today),
      TaskCompletion("h2", today),
      TaskCompletion("h1", LocalDate(2026, 1, 14)),
      TaskCompletion("h2", LocalDate(2026, 1, 14)),
      TaskCompletion("h1", LocalDate(2026, 1, 13)),
      TaskCompletion("h2", LocalDate(2026, 1, 13)),
      TaskCompletion("h1", LocalDate(2026, 1, 12)),
      // h2 didn't exist on day 12, so it shouldn't break the streak
      TaskCompletion("h1", LocalDate(2026, 1, 11)),
      TaskCompletion("h1", LocalDate(2026, 1, 10)),
    )

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit1, habit2),
      completions = completions,
      today = today,
    )

    assertEquals(6, streak)
  }

  @Test
  fun `it should handle long streaks correctly`() {
    val today = LocalDate(2026, 1, 30)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 1),
    )

    // Create completions for 30 days
    val completions = (0..29).map { daysAgo ->
      TaskCompletion("h1", LocalDate(2026, 1, 30 - daysAgo))
    }

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(30, streak)
  }

  @Test
  fun `it should return 0 when habit exists but never completed`() {
    val today = LocalDate(2026, 1, 15)
    val habit = HabitInfo(
      id = "h1",
      createdAt = LocalDate(2026, 1, 10),
    )
    val completions = emptyList<TaskCompletion>()

    val streak = streakCalculator.calculateCurrentStreak(
      habits = listOf(habit),
      completions = completions,
      today = today,
    )

    assertEquals(0, streak)
  }
}
