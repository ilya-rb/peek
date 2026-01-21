package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate

public data class TaskDraft(
  val title: String,
  val habit: Boolean,
  val timeOfDay: TimeOfDay,
  val forDate: LocalDate,
)
