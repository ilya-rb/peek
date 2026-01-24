package com.illiarb.peek.uikit.core.atom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitColors

@Composable
public fun IconCounter(
  icon: VectorIcon,
  value: Int,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.padding(end = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon.imageVector,
      contentDescription = icon.contentDescription,
      tint = icon.tint ?: LocalContentColor.current,
    )
    Text(
      text = value.toString(),
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
    )
  }
}

@Preview
@Composable
private fun IconCounterPreviewLight() {
  PreviewTheme(darkMode = false) {
    IconCounter(
      icon = VectorIcon(
        imageVector = Icons.Filled.LocalFireDepartment,
        contentDescription = "",
        tint = UiKitColors.orange,
      ),
      value = 8,
    )
  }
}

@Preview
@Composable
private fun IconCounterPreviewDark() {
  PreviewTheme(darkMode = true) {
    IconCounter(
      icon = VectorIcon(
        imageVector = Icons.Filled.LocalFireDepartment,
        contentDescription = "",
        tint = UiKitColors.orange,
      ),
      value = 8,
    )
  }
}
