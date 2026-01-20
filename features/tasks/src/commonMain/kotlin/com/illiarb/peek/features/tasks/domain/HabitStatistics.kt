package com.illiarb.peek.features.tasks.domain

import kotlin.time.Instant

public data class HabitStatistics(
  val currentStreak: Int,
)

internal data class HabitInfo(
  val id: String,
  val createdAt: Instant,
)

