package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate

/**
 * Represents habit completion history for a single day.
 * Used for rendering the habit history list.
 *
 * Example rendering:
 * ```
 * Jan 15 - 4/5 completed
 *   ✓ Morning workout
 *   ✓ Read 30 min
 *   ✓ Meditate
 *   ✓ Journal
 * ```
 */
public data class DayHistory(
  val date: LocalDate,
  val completedHabits: List<Task>,
  val totalHabits: Int,
)
