package com.illiarb.catchup.uikit.core.text

import androidx.compose.runtime.Composable
import kotlin.time.Duration

sealed interface Clause {

  data class TextClause(val clause: String) : Clause

  data class DurationClause(val duration: Duration) : Clause

  data class AuthorClause(val author: String) : Clause
}

interface ClauseDisplayer {

  @Composable
  fun render(clause: Clause)
}