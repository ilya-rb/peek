package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
public fun EmptyState(
  modifier: Modifier = Modifier,
  title: String,
  buttonText: String?,
  buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
  onButtonClick: () -> Unit = {},
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
      modifier = Modifier.padding(
        top = 16.dp,
        bottom = if (buttonText == null) 24.dp else 0.dp
      ),
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center,
      maxLines = 2,
    )

    if (buttonText != null) {
      Button(
        modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
        onClick = onButtonClick,
        colors = buttonColors,
      ) {
        Text(text = buttonText)
      }
    }
  }
}
