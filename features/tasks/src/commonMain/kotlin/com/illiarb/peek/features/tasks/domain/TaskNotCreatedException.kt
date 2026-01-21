package com.illiarb.peek.features.tasks.domain

internal data class TaskNotCreatedException(
  val kind: ErrorKind
) : Throwable() {

  sealed interface ErrorKind {
    data object NameTooLong : ErrorKind
  }
}
