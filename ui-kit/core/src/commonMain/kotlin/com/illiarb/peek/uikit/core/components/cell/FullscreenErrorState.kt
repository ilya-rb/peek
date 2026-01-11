package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.common_error_default_action
import com.illiarb.peek.uikit.resources.common_error_default_title
import org.jetbrains.compose.resources.stringResource

@Composable
public fun FullscreenErrorState(
  modifier: Modifier = Modifier,
  onActionClick: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    EmptyState(
      title = stringResource(Res.string.common_error_default_title),
      buttonText = stringResource(Res.string.common_error_default_action),
      buttonColors = ButtonDefaults.buttonColors().copy(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
      ),
      onButtonClick = onActionClick,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clip(shape = RoundedCornerShape(size = 24.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
      Icon(
        modifier = Modifier.size(120.dp).padding(top = 24.dp),
        imageVector = Icons.Outlined.Error,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
      )
    }
  }
}
