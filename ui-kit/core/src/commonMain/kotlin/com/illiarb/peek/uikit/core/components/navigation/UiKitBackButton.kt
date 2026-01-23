package com.illiarb.peek.uikit.core.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_navigation_back
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UiKitBackButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = onClick,
    modifier = modifier,
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = stringResource(Res.string.acsb_navigation_back),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(8.dp),
    )
  }
}

@Preview
@Composable
private fun UiKitBackButtonPreviewLight() {
  PreviewTheme(darkMode = false) {
    UiKitBackButton({})
  }
}

@Preview
@Composable
private fun UiKitBackButtonPreviewDark() {
  PreviewTheme(darkMode = true) {
    UiKitBackButton({})
  }
}
