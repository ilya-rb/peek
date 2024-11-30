package com.illiarb.catchup.features.home.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.service.domain.Tag
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator

expect suspend fun OverlayHost.showFiltersOverlay(model: FiltersOverlayModel): Set<Tag>

data class FiltersOverlayModel(
  val tags: Set<Tag>,
  val selectedTags: Set<Tag>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersOverlay(model: FiltersOverlayModel, navigator: OverlayNavigator<Set<Tag>>) {
  val selectedTags by remember { mutableStateOf(model.selectedTags.toMutableSet()) }

  Column {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp),
      text = "Tags",
      style = MaterialTheme.typography.headlineMedium,
    )

    FlowRow(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      model.tags.map { tag ->
        var isSelected by remember { mutableStateOf(tag in selectedTags) }

        FilterChip(
          selected = isSelected,
          onClick = {
            isSelected = !isSelected

            val added = selectedTags.add(tag)
            if (added.not()) {
              selectedTags.remove(tag)
            }
          },
          label = { Text(tag.value) },
        )
      }
    }

    Button(
      onClick = { navigator.finish(selectedTags) },
      modifier = Modifier.fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp),
    ) {
      Text(text = "Confirm")
    }
  }
}