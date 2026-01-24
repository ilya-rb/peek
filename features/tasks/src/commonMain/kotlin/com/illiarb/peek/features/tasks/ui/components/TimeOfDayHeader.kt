package com.illiarb.peek.features.tasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.illiarb.peek.features.tasks.domain.TimeOfDay
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Anytime
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Evening
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Midday
import com.illiarb.peek.features.tasks.domain.TimeOfDay.Morning
import com.illiarb.peek.uikit.core.components.cell.ListHeader
import com.illiarb.peek.uikit.core.components.cell.ListHeaderStyle
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_icon_collapse
import com.illiarb.peek.uikit.resources.acsb_icon_expand
import com.illiarb.peek.uikit.resources.tasks_section_completed
import com.illiarb.peek.uikit.resources.tasks_section_evening
import com.illiarb.peek.uikit.resources.tasks_section_midday
import com.illiarb.peek.uikit.resources.tasks_section_morning
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TimeOfDayHeader(
  timeOfDay: TimeOfDay,
  isExpanded: Boolean,
  allTasksCompleted: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ListHeader(
    title = { style, modifier ->
      Text(stringResource(timeOfDay.getSectionTitle()), modifier, style = style)

      if (allTasksCompleted) {
        Text(
          text = " · ${stringResource(Res.string.tasks_section_completed)}",
          style = style,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
      }
    },
    style = ListHeaderStyle.Small,
    modifier = modifier.clickable(onClick = onToggle),
    startIcon = VectorIcon(
      imageVector = timeOfDay.getIcon(),
      contentDescription = timeOfDay.name,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
    endIcon = VectorIcon(
      imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      contentDescription = if (isExpanded) {
        stringResource(Res.string.acsb_icon_collapse)
      } else {
        stringResource(Res.string.acsb_icon_expand)
      },
    ),
  )
}

private fun TimeOfDay.getSectionTitle() = when (this) {
  Morning -> Res.string.tasks_section_morning
  Midday -> Res.string.tasks_section_midday
  Evening -> Res.string.tasks_section_evening
  Anytime -> throw IllegalArgumentException("Anytime not supported")
}

private fun TimeOfDay.getIcon(): ImageVector = when (this) {
  Morning -> Icons.Filled.WbTwilight
  Midday -> Icons.Filled.WbSunny
  Evening -> Icons.Filled.Brightness3
  Anytime -> throw IllegalArgumentException("Anytime not supported")
}
