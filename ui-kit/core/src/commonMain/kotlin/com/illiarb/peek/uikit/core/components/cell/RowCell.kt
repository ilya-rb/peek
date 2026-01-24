package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.components.cell.internal.RowCellInternal
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes
import org.jetbrains.compose.resources.DrawableResource

@Immutable
public sealed interface StartImage {

  public data class Icon(val icon: VectorIcon) : StartImage

  public data class Avatar(val image: DrawableResource) : StartImage
}

@Immutable
public sealed interface EndAction {

  public data class Icon(
    val icon: VectorIcon,
    val modifier: Modifier = Modifier,
  ) : EndAction

  public data class Action(
    val text: String,
    val onClick: () -> Unit,
  ) : EndAction
}

@Composable
public fun RowCell(
  modifier: Modifier = Modifier,
  title: String,
  subtitle: String? = null,
  startImage: StartImage? = null,
  endAction: EndAction? = null,
) {
  RowCellInternal(
    modifier = modifier,
    title = {
      Text(text = title, style = MaterialTheme.typography.bodyLarge)
    },
    subtitle = {
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    startContent = { modifier ->
      if (startImage != null) {
        when (startImage) {
          is StartImage.Icon -> {
            Icon(
              startImage.icon.imageVector,
              startImage.icon.contentDescription,
              modifier,
            )
          }

          is StartImage.Avatar -> {
            SelectableCircleAvatar(
              modifier = modifier,
              image = startImage.image,
              state = AvatarState.Default,
              onClick = {},
            )
          }
        }
      }
    },
    endContent = {
      if (endAction != null) {
        when (endAction) {
          is EndAction.Action -> {
            TextButton(
              onClick = endAction.onClick,
              content = { Text(text = endAction.text) },
            )
          }

          is EndAction.Icon -> {
            Icon(
              imageVector = endAction.icon.imageVector,
              contentDescription = endAction.icon.contentDescription,
              modifier = endAction.modifier,
              tint = endAction.icon.tint ?: LocalContentColor.current,
            )
          }
        }
      }
    }
  )
}

@Preview
@Composable
private fun RowCellFullPreviewLight() {
  PreviewTheme(darkMode = false) {
    RowCellFullPreview()
  }
}

@Preview
@Composable
private fun RowCellFullPreviewDark() {
  PreviewTheme(darkMode = true) {
    RowCellFullPreview()
  }
}

@Composable
private fun RowCellFullPreview() {
  RowCell(
    title = "Settings",
    subtitle = "Configure your preferences",
    startImage = StartImage.Icon(
      VectorIcon(
        imageVector = Icons.Filled.Settings,
        contentDescription = "Settings icon"
      )
    ),
    endAction = EndAction.Action(
      text = "Edit",
      onClick = {}
    )
  )
}

@Preview
@Composable
private fun RowCellStatesPreview() {
  PreviewTheme(darkMode = false) {
    RowCellStatesPreviewContent()
  }
}

@Composable
private fun RowCellStatesPreviewContent() {
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    RowPreviewBox {
      RowCell(
        title = "Title only",
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle",
        subtitle = "Additional information"
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With icon",
        startImage = StartImage.Icon(
          VectorIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Icon"
          )
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With action",
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle and icon",
        subtitle = "Subtitle text",
        startImage = StartImage.Icon(
          VectorIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Icon"
          )
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With subtitle and action",
        subtitle = "Subtitle text",
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = "With icon and action",
        startImage = StartImage.Icon(
          VectorIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Icon"
          )
        ),
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }
  }
}

@Composable
private fun RowPreviewBox(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceContainer,
    shape = UiKitShapes.medium,
  ) {
    content()
  }
}
