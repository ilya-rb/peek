package com.illiarb.peek.uikit.core.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun <T> SelectableChipGroup(
  options: ImmutableList<T>,
  selectedOption: T,
  onOptionSelected: (T) -> Unit,
  labelProvider: (T) -> String,
  modifier: Modifier = Modifier,
) {
  FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    options.forEach { option ->
      FilterChip(
        selected = option == selectedOption,
        onClick = {
          onOptionSelected(option)
        },
        label = {
          Text(text = labelProvider(option))
        },
      )
    }
  }
}
