package com.illiarb.peek.core.workscheduler

public interface WorkScheduler

public interface Worker {

  public val scheduleOnStartup: Boolean

  public suspend fun config(): WorkConfiguration

  public suspend fun doWork(): Result

  public sealed interface Result {
    public data object Success : Result
    public data object Failure : Result
  }
}

