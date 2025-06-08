package com.illiarb.catchup.features.home.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.uikit.core.components.cell.RowCell
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_icon_tags
import com.illiarb.catchup.uikit.resources.filters_action_cancel
import com.illiarb.catchup.uikit.resources.filters_action_reset
import com.illiarb.catchup.uikit.resources.filters_action_save
import com.illiarb.catchup.uikit.resources.filters_header_tags
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator
import org.jetbrains.compose.resources.stringResource

internal expect suspend fun OverlayHost.showTagFilterOverlay(input: TagFilterContract.Input): TagFilterContract.Output

internal interface TagFilterContract {

  data class Input(
    val allTags: Set<Tag>,
    val selectedTags: Set<Tag>,
    val containerColor: Color,
  )

  sealed interface Output {
    data class Saved(val selectedTags: Set<Tag>) : Output
    data object Cancel : Output
  }
}

@Composable
internal fun TagFilterOverlay(
  input: TagFilterContract.Input,
  navigator: OverlayNavigator<TagFilterContract.Output>,
) {
  val selectedTags = remember {
    input.selectedTags
      .map { it to true }
      .toMutableStateMap()
  }
  val scrollState = rememberScrollState()

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    bottomBar = {
      BottomAppBar(
        actions = {
          Button(
            modifier = Modifier.padding(start = 16.dp),
            content = { Text(stringResource(Res.string.filters_action_save)) },
            onClick = { navigator.finish(TagFilterContract.Output.Saved(selectedTags.keys)) },
          )
          Button(
            modifier = Modifier.padding(start = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
            content = { Text(stringResource(Res.string.filters_action_cancel)) },
            onClick = { navigator.finish(TagFilterContract.Output.Cancel) },
          )
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(input.containerColor)
        .padding(innerPadding)
        .verticalScroll(scrollState)
    ) {
      RowCell(
        startIcon = Icons.Filled.Tag,
        startIconContentDescription = stringResource(Res.string.acsb_icon_tags),
        text = stringResource(Res.string.filters_header_tags),
        endActionText = stringResource(Res.string.filters_action_reset),
        onEndActionClicked = { selectedTags.clear() },
      )

      HorizontalDivider()

      FlowRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        input.allTags.map { tag ->
          val selected = selectedTags[tag] == true

          FilterChip(
            selected = selected,
            label = { Text(tag.value) },
            onClick = { selectedTags[tag] = !selected },
          )
        }
      }
    }
  }
}