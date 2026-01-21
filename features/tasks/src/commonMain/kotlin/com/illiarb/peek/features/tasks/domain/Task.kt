package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate

public data class Task(
  val id: String,
  val title: String,
  val habit: Boolean,
  val timeOfDay: TimeOfDay,
  val createdAt: LocalDate,
  val createdForDate: LocalDate,
  val archived: Boolean = false,
  val completed: Boolean = false,
) {

  internal companion object {
    const val TASK_NAME_LIMIT = 300
  }
}

internal data class TaskCompletion(
  val taskId: String,
  val date: LocalDate,
)

