package com.illiarb.catchup.uikit.core.text

import androidx.compose.runtime.Composable
import kotlin.time.Duration

public sealed interface Clause {

  public data class TextClause(val clause: String) : Clause

  public data class DurationClause(val duration: Duration) : Clause

  public data class AuthorClause(val author: String) : Clause
}

public interface ClauseDisplayer {

  @Composable
  public fun render(clause: Clause)
}