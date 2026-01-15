package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
public fun SectionHeader(
  title: String,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  iconContentDescription: String? = null,
) {
  Row(
    modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = iconContentDescription,
        modifier = Modifier.size(20.dp),
        tint = MaterialTheme.colorScheme.primary,
      )
    }

    Text(
      text = title,
      modifier = Modifier.padding(start = if (icon != null) 8.dp else 0.dp),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}
