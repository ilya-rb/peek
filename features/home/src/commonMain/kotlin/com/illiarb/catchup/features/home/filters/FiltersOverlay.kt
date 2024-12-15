package com.illiarb.catchup.features.home.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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

internal expect suspend fun OverlayHost.showFiltersOverlay(
  model: FiltersOverlayModel
): FiltersOverlayResult

internal sealed interface FiltersOverlayResult {
  data class Saved(val tags: Set<Tag>) : FiltersOverlayResult
  data object Cancel : FiltersOverlayResult
}

internal data class FiltersOverlayModel(
  val tags: Set<Tag>,
  val selectedTags: Set<Tag>,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FiltersOverlay(
  model: FiltersOverlayModel,
  navigator: OverlayNavigator<FiltersOverlayResult>,
) {
  val selectedTags by remember { mutableStateOf(model.selectedTags.toMutableSet()) }

  Column {
    Text(
      modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
      text = "Tags",
      style = MaterialTheme.typography.headlineSmall,
    )

    HorizontalDivider()

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

    HorizontalDivider()

    Row(Modifier.navigationBarsPadding().padding(top = 16.dp)) {
      Button(
        onClick = { navigator.finish(FiltersOverlayResult.Saved(selectedTags)) },
        modifier = Modifier.padding(start = 16.dp),
      ) {
        Text(text = "Save")
      }

      Button(
        onClick = { navigator.finish(FiltersOverlayResult.Cancel) },
        modifier = Modifier.padding(start = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(),
      ) {
        Text(text = "Cancel")
      }
    }
  }
}