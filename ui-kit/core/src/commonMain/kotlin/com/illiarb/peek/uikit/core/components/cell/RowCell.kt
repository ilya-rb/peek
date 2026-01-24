package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.AvatarState
import com.illiarb.peek.uikit.core.atom.SelectableCircleAvatar
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.EndAction
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.StartContent
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.TextModel
import com.illiarb.peek.uikit.core.components.cell.internal.RowCellInternal
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.preview.PreviewTheme
import com.illiarb.peek.uikit.core.theme.UiKitShapes
import com.illiarb.peek.uikit.resources.Res
import com.illiarb.peek.uikit.resources.acsb_switch_checked
import com.illiarb.peek.uikit.resources.hn_logo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

public interface RowCellContract {

  @Immutable
  public sealed interface StartContent {

    public data class Icon(val icon: VectorIcon) : StartContent

    public data class Avatar(val image: DrawableResource) : StartContent

    public data class Checkbox(
      val checked: Boolean,
      val enabled: Boolean,
    ) : StartContent
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

    public data class Switch(val checked: Boolean) : EndAction
  }

  public data class TextModel(
    val text: String,
    val decoration: TextDecoration? = null,
  )
}

@Composable
public fun RowCell(
  modifier: Modifier = Modifier,
  title: TextModel,
  subtitle: TextModel? = null,
  startContent: StartContent? = null,
  endAction: EndAction? = null,
) {
  RowCellInternal(
    modifier = modifier.padding(vertical = 8.dp),
    title = {
      Text(
        text = title.text,
        style = MaterialTheme.typography.bodyLarge,
        textDecoration = title.decoration,
      )
    },
    subtitle = {
      if (subtitle != null) {
        Text(
          text = subtitle.text,
          textDecoration = subtitle.decoration,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    startContent = { modifier ->
      if (startContent != null) {
        when (startContent) {
          is StartContent.Icon -> {
            Icon(
              startContent.icon.imageVector,
              startContent.icon.contentDescription,
              modifier,
            )
          }

          is StartContent.Avatar -> {
            SelectableCircleAvatar(
              modifier = modifier,
              image = startContent.image,
              state = AvatarState.Default,
              onClick = {},
            )
          }

          is StartContent.Checkbox -> {
            Checkbox(
              modifier = modifier,
              checked = startContent.checked,
              onCheckedChange = null,
              enabled = true,
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

          is EndAction.Switch -> {
            Switch(
              checked = endAction.checked,
              onCheckedChange = null,
              thumbContent = {
                if (endAction.checked) {
                  Icon(
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(Res.string.acsb_switch_checked),
                  )
                }
              }
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
    title = TextModel("Settings"),
    subtitle = TextModel("Configure your preferences"),
    startContent = StartContent.Icon(
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
        title = TextModel("Title only"),
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With subtitle"),
        subtitle = TextModel("Additional information")
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With icon"),
        startContent = StartContent.Icon(
          VectorIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Icon"
          )
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With action"),
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With subtitle and icon"),
        subtitle = TextModel("Subtitle text"),
        startContent = StartContent.Icon(
          VectorIcon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Icon"
          )
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With subtitle and action"),
        subtitle = TextModel("Subtitle text"),
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With icon and action"),
        startContent = StartContent.Icon(
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

    RowPreviewBox {
      RowCell(
        title = TextModel("With checkbox"),
        startContent = StartContent.Checkbox(
          checked = true,
          enabled = false,
        ),
        endAction = EndAction.Action(
          text = "Action",
          onClick = {}
        )
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With switch and avatar"),
        startContent = StartContent.Avatar(Res.drawable.hn_logo),
        endAction = EndAction.Switch(true),
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
