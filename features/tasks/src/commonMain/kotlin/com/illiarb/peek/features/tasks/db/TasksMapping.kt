package com.illiarb.peek.features.tasks.db

import com.illiarb.peek.core.data.ext.toLocalDate
import com.illiarb.peek.core.data.ext.toLocalDateTime
import com.illiarb.peek.features.tasks.OverdueTasksForDate
import com.illiarb.peek.features.tasks.TasksForDateWithCompletion
import com.illiarb.peek.features.tasks.domain.Task
import com.illiarb.peek.features.tasks.domain.TimeOfDay

internal fun TasksForDateWithCompletion.asTask(): Task {
  return Task(
    id = id,
    title = title,
    habit = is_habit == 1L,
    timeOfDay = TimeOfDay.fromString(time_of_day),
    createdAt = created_at.toLocalDateTime(),
    createdForDate = created_for_date.toLocalDate(),
    archived = archived == 1L,
    completed = is_completed == 1L,
  )
}

internal fun OverdueTasksForDate.asTask(): Task {
  return Task(
    id = id,
    title = title,
    habit = is_habit == 1L,
    timeOfDay = TimeOfDay.fromString(time_of_day),
    createdAt = created_at.toLocalDateTime(),
    createdForDate = created_for_date.toLocalDate(),
    archived = archived == 1L,
    completed = is_completed == 1L,
  )
}
