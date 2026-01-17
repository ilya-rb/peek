package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.model.VectorIcon
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.common_error_default_action
import com.illiarb.peek.uikit.resources.common_error_default_title
import org.jetbrains.compose.resources.stringResource

@Composable
public fun EmptyState(
  title: String,
  modifier: Modifier = Modifier,
  image: VectorIcon? = null,
  buttonText: String? = null,
  buttonIcon: VectorIcon? = null,
  onButtonClick: () -> Unit = {},
) {
  EmptyStateInternal(title, modifier, image, buttonText, buttonIcon, onButtonClick = onButtonClick)
}

@Composable
public fun ErrorEmptyState(
  title: String = stringResource(Res.string.common_error_default_title),
  modifier: Modifier = Modifier,
  image: VectorIcon? = VectorIcon(
    imageVector = Icons.Outlined.Error,
    contentDescription = "",
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onError),
  ),
  buttonText: String = stringResource(Res.string.common_error_default_action),
  buttonIcon: VectorIcon? = null,
  onButtonClick: () -> Unit = {},
) {
  EmptyStateInternal(
    title,
    modifier,
    image,
    buttonText,
    buttonIcon,
    iconsColor = MaterialTheme.colorScheme.error,
    buttonColors = ButtonDefaults.buttonColors().copy(
      containerColor = MaterialTheme.colorScheme.error,
      contentColor = MaterialTheme.colorScheme.onError,
    ),
    onButtonClick
  )
}

@Composable
internal fun EmptyStateInternal(
  title: String,
  modifier: Modifier = Modifier,
  image: VectorIcon? = null,
  buttonText: String? = null,
  buttonIcon: VectorIcon? = null,
  iconsColor: Color = MaterialTheme.colorScheme.onSurface,
  buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
  onButtonClick: () -> Unit = {},
) {
  Surface(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceContainer,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      if (image != null) {
        Image(
          modifier = Modifier.size(120.dp).padding(top = 16.dp),
          imageVector = image.imageVector,
          contentDescription = image.contentDescription,
          colorFilter = ColorFilter.tint(iconsColor),
        )
      }

      Text(
        text = title,
        modifier = Modifier.padding(top = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 2,
      )

      if (buttonText != null) {
        Button(
          modifier = Modifier.padding(vertical = 16.dp),
          onClick = onButtonClick,
          colors = buttonColors,
        ) {
          if (buttonIcon != null) {
            Image(
              imageVector = buttonIcon.imageVector,
              contentDescription = buttonIcon.contentDescription,
            )
          }
          Text(
            modifier = Modifier.padding(start = 8.dp),
            text = buttonText,
          )
        }
      }
    }
  }
}
