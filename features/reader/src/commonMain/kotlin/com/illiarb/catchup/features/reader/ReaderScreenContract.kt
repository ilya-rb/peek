package com.illiarb.catchup.features.reader

import androidx.compose.runtime.Stable
import com.illiarb.catchup.core.arch.CommonParcelable
import com.illiarb.catchup.core.arch.CommonParcelize
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen

interface ReaderScreenContract {

  @CommonParcelize
  object ReaderScreen : Screen, CommonParcelable

  @Stable
  data object State : CircuitUiState
}