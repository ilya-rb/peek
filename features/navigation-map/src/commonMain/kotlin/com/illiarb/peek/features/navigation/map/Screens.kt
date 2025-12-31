package com.illiarb.peek.features.navigation.map

import androidx.compose.runtime.Immutable
import com.illiarb.peek.core.arch.CommonParcelable
import com.illiarb.peek.core.arch.CommonParcelize
import com.illiarb.peek.core.types.Url
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen

@CommonParcelize
@Immutable
public object HomeScreen : Screen, CommonParcelable

@CommonParcelize
@Immutable
public object BookmarksScreen : Screen, CommonParcelable

@CommonParcelize
@Immutable
public object ServicesScreen : Screen, CommonParcelable {

  @CommonParcelize
  public data object Result : PopResult, CommonParcelable
}

@CommonParcelize
@Immutable
public data object SettingsScreen : Screen, CommonParcelable

@CommonParcelize
@Immutable
public data class ReaderScreen(val url: Url) : Screen, CommonParcelable

@CommonParcelize
@Immutable
public data class SummaryScreen(
  val url: Url,
  val context: Context,
) : Screen, CommonParcelable {

  public enum class Context {
    HOME,
    READER,
  }

  public sealed interface Result : PopResult, CommonParcelable {

    @CommonParcelize
    public data object Close : Result

    @CommonParcelize
    public data class OpenInReader(val url: Url) : Result
  }
}
