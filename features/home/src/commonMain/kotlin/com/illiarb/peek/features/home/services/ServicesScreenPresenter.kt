package com.illiarb.peek.features.home.services

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.illiarb.peek.api.PeekApiService
import com.illiarb.peek.api.domain.NewsSource
import com.illiarb.peek.core.data.Async
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class ServicesScreenPresenter(
  private val peekApiService: PeekApiService,
) : Presenter<ServicesScreenContract.State> {

  @Composable
  override fun present(): ServicesScreenContract.State {
    val coroutineScope = rememberStableCoroutineScope()

    val sources by produceRetainedState<Async<ImmutableList<NewsSource>>>(Async.Loading) {
      peekApiService.collectAvailableSources().collect {
        value = it.map { sources -> sources.toImmutableList() }
      }
    }

    return ServicesScreenContract.State(sources) { event ->
      when (event) {
        is ServicesScreenContract.Event.ItemsReordered -> coroutineScope.launch {
          peekApiService.updateAvailableSources(event.items)
        }
      }
    }
  }
}
