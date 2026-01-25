package com.illiarb.peek.features.tasks.domain

import kotlinx.datetime.LocalDate

public data class HabitStatistics(
  val currentStreak: Int,
)

internal data class HabitInfo(
  val id: String,
  val createdForDate: LocalDate,
)

