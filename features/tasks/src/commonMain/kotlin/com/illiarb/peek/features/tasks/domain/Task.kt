package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/**
 * Represents a task in the tasks feature.
 *
 * Task Types:
 * - Regular Task: [habit] = false, [createdForDate] = specific date
 *   - Appears only on the date it was created for
 *   - Once completed or the day passes, it's done
 *
 * - Habit: [habit] = true, [createdForDate] = null
 *   - Appears every day
 *   - Completion is tracked per day
 *   - History can be viewed by filtering completions
 */
public data class Task(
  val id: String,
  val title: String,
  val habit: Boolean,
  val timeOfDay: TimeOfDay,
  val createdAt: Instant,
  val createdForDate: LocalDate?,
  val archived: Boolean = false,
  val completed: Boolean = false,
)

internal data class TaskCompletion(
  val taskId: String,
  val date: LocalDate,
)

