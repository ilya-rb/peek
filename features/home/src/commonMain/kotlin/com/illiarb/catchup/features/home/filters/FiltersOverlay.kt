package com.illiarb.catchup.features.home.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.features.home.ArticlesFilter
import com.illiarb.catchup.service.domain.Tag
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_icon_bookmarked
import com.illiarb.catchup.uikit.resources.acsb_icon_tags
import com.illiarb.catchup.uikit.resources.filters_action_cancel
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
  val selectedTags by remember {
    mutableStateOf(
      model.filter.filters
        .filterIsInstance<ArticlesFilter.ByTag>()
        .firstOrNull()
        ?.tags
        .orEmpty()
        .toMutableSet()
    )
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
                      ArticlesFilter.ByTag(selectedTags),
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(all = 16.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.Bookmark,
          contentDescription = stringResource(Res.string.acsb_icon_bookmarked)
        )
        Text(
          text = stringResource(Res.string.filters_header_bookmarked_only),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(start = 16.dp),
        )
        Spacer(Modifier.weight(1f))
        Switch(
          checked = bookmarkedOnly,
          onCheckedChange = { checked ->
            bookmarkedOnly = checked
          }
        )
      }
      HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
      Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          imageVector = Icons.Filled.Tag,
          contentDescription = stringResource(Res.string.acsb_icon_tags),
        )
        Text(
          modifier = Modifier.padding(start = 16.dp),
          text = stringResource(Res.string.filters_header_tags),
          style = MaterialTheme.typography.bodyLarge,
        )
      }
      if (model.availableTags.isNotEmpty()) {
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

        FlowRow(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          model.availableTags.map { tag ->
            var isSelected by remember { mutableStateOf(tag in selectedTags) }

            FilterChip(
              selected = isSelected,
              label = { Text(tag.value) },
              onClick = {
                isSelected = !isSelected

                val added = selectedTags.add(tag)
                if (added.not()) {
                  selectedTags.remove(tag)
                }
              },
            )
          }
        }
      }
    }
  }
}