package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate

/**
 * A draft for creating a new task.
 * Used as input when creating a new task - keeps the service interface clean.
 *
 * @property title The task name/description
 * @property habit If true, this task will appear every day as a habit
 * @property timeOfDay Optional time of day (morning, midday, evening), null means "Anytime"
 * @property forDate For regular tasks: the date they're for; for habits: null
 */
public data class TaskDraft(
  val title: String,
  val habit: Boolean,
  val timeOfDay: TimeOfDay?,
  val forDate: LocalDate?,
)
