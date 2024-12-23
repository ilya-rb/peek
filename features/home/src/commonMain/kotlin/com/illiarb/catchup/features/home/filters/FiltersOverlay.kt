package com.illiarb.catchup.features.home.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.features.home.ArticlesFilter
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.uikit.core.components.cell.RowCell
import com.illiarb.catchup.uikit.core.components.cell.SwitchCell
import com.illiarb.catchup.uikit.core.model.VectorIcon
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_icon_bookmarked
import com.illiarb.catchup.uikit.resources.acsb_icon_tags
import com.illiarb.catchup.uikit.resources.filters_action_cancel
import com.illiarb.catchup.uikit.resources.filters_action_reset
import com.illiarb.catchup.uikit.resources.filters_action_save
import com.illiarb.catchup.uikit.resources.filters_header_bookmarked_only
import com.illiarb.catchup.uikit.resources.filters_header_tags
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuit.overlay.OverlayNavigator
import org.jetbrains.compose.resources.stringResource

internal interface FiltersContract {

  data class Model(
    val availableTags: Set<Tag>,
    val filter: ArticlesFilter.Composite,
  )

  sealed interface Result {
    data class Saved(val filter: ArticlesFilter.Composite) : Result
    data object Cancel : Result
  }
}

internal expect suspend fun OverlayHost.showFiltersOverlay(
  model: FiltersContract.Model,
  containerColor: Color,
): FiltersContract.Result

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FiltersOverlay(
  model: FiltersContract.Model,
  navigator: OverlayNavigator<FiltersContract.Result>,
) {
  val selectedTags = remember {
    model.filter.filters
      .filterIsInstance<ArticlesFilter.ByTag>()
      .firstOrNull()
      ?.tags
      .orEmpty()
      .map { it to true }
      .toMutableStateMap()
  }
  var bookmarkedOnly by remember {
    mutableStateOf(
      model.filter.filters
        .filterIsInstance<ArticlesFilter.Saved>()
        .isNotEmpty()
    )
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
            onClick = {
              navigator.finish(
                FiltersContract.Result.Saved(
                  ArticlesFilter.Composite(
                    filters = setOfNotNull(
                      ArticlesFilter.Saved.takeIf { bookmarkedOnly },
                      ArticlesFilter.ByTag(selectedTags.keys),
                    )
                  )
                )
              )
            },
          )
          Button(
            modifier = Modifier.padding(start = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(),
            content = { Text(stringResource(Res.string.filters_action_cancel)) },
            onClick = { navigator.finish(FiltersContract.Result.Cancel) },
          )
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .padding(innerPadding)
        .verticalScroll(scrollState)
    ) {
      SwitchCell(
        text = stringResource(Res.string.filters_header_bookmarked_only),
        startIcon = VectorIcon(
          imageVector = Icons.Filled.Bookmark,
          contentDescription = stringResource(Res.string.acsb_icon_bookmarked),
        ),
        switchChecked = bookmarkedOnly,
        onChecked = { checked -> bookmarkedOnly = checked }
      )

      HorizontalDivider()

      if (model.availableTags.isNotEmpty()) {
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
          model.availableTags.map { tag ->
            val selected = selectedTags[tag] == true

            FilterChip(
              selected = selected,
              label = { Text(tag.value) },
              onClick = {
                selectedTags[tag] = !selected
              },
            )
          }
        }
      }
    }
  }
}