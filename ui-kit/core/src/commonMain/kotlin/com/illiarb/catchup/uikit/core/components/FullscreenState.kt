package com.illiarb.catchup.uikit.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FullscreenState(
  modifier: Modifier = Modifier,
  title: String,
  buttonText: String?,
  onButtonClick: () -> Unit,
  image: @Composable (Modifier) -> Unit,
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    image(Modifier)

    Text(
      text = title,
      modifier = Modifier.padding(top = 20.dp),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onSurface,
    )

    if (buttonText != null) {
      Button(
        modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
        onClick = onButtonClick,
      ) {
        Text(text = buttonText)
      }
    }
  }
}
