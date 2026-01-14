package com.illiarb.peek.features.tasks.di

import app.cash.sqldelight.db.SqlDriver
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.features.tasks.DefaultTasksService
import com.illiarb.peek.features.tasks.TasksDatabase
import com.illiarb.peek.features.tasks.TasksService
import com.illiarb.peek.features.tasks.repository.TasksRepository
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn

@BindingContainer(
  includes = [
    TasksBindingsInternal::class,
    TasksPlatformBindings::class,
  ]
)
public object TasksBindings

@BindingContainer
internal object TasksBindingsInternal {

  @Provides
  fun provideTasksService(repository: TasksRepository): TasksService {
    return DefaultTasksService(repository)
  }

  @Provides
  @InternalApi
  @SingleIn(AppScope::class)
  fun provideTasksDatabase(@InternalApi driver: SqlDriver): TasksDatabase {
    return TasksDatabase(driver)
  }
}

@BindingContainer
internal expect object TasksPlatformBindings

@Qualifier
internal annotation class InternalApi
