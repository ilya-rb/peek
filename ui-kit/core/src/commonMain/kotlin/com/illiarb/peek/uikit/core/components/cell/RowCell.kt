package com.illiarb.peek.uikit.core.components.cell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.core.atom.AvatarState
import com.illiarb.peek.uikit.core.atom.SelectableCircleAvatar
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.EndContent
import com.illiarb.peek.uikit.core.components.cell.RowCellContract.StartContent
import com.illiarb.peek.uikit.core.image.VectorIcon
import com.illiarb.peek.uikit.core.model.TextModel
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
  public sealed interface EndContent {

    public data class Icon(
      val icon: VectorIcon,
      val modifier: Modifier = Modifier,
    ) : EndContent

    public data class Content(
      val text: String,
      val onClick: () -> Unit,
    ) : EndContent

    public data class Switch(val checked: Boolean) : EndContent
  }

  public sealed interface Style {
    public data object Condensed : Style
    public data object Regular : Style
  }
}

@Composable
public fun RowCell(
  modifier: Modifier = Modifier,
  title: TextModel,
  subtitle: TextModel? = null,
  startContent: StartContent? = null,
  endContent: EndContent? = null,
  style: RowCellContract.Style = RowCellContract.Style.Regular,
) {
  val verticalSpacing = when (style) {
    RowCellContract.Style.Condensed -> 8.dp
    RowCellContract.Style.Regular -> 16.dp
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.padding(horizontal = 16.dp, vertical = verticalSpacing),
  ) {
    if (startContent != null) {
      val startContentModifier = Modifier.padding(end = 16.dp)

      when (startContent) {
        is StartContent.Icon -> {
          Icon(
            startContent.icon.imageVector,
            startContent.icon.contentDescription,
            startContentModifier,
          )
        }

        is StartContent.Avatar -> {
          SelectableCircleAvatar(
            modifier = startContentModifier,
            size = 40.dp,
            image = startContent.image,
            state = AvatarState.Default,
            onClick = {},
          )
        }

        is StartContent.Checkbox -> {
          Checkbox(
            modifier = startContentModifier,
            checked = startContent.checked,
            onCheckedChange = null,
            enabled = true,
          )
        }
      }
    }

    Column {
      Text(
        text = title.text,
        style = MaterialTheme.typography.bodyLarge,
        textDecoration = title.decoration,
        color = title.color ?: Color.Unspecified,
      )

      if (subtitle != null) {
        Text(
          text = subtitle.text,
          textDecoration = subtitle.decoration,
          style = MaterialTheme.typography.bodyMedium,
          color = subtitle.color ?: MaterialTheme.colorScheme.primary,
        )
      }
    }

    Spacer(Modifier.weight(1f))

    if (endContent != null) {
      when (endContent) {
        is EndContent.Content -> {
          TextButton(
            onClick = endContent.onClick,
            content = { Text(text = endContent.text) },
          )
        }

        is EndContent.Icon -> {
          Icon(
            imageVector = endContent.icon.imageVector,
            contentDescription = endContent.icon.contentDescription,
            modifier = endContent.modifier,
            tint = endContent.icon.tint ?: LocalContentColor.current,
          )
        }

        is EndContent.Switch -> {
          Switch(
            checked = endContent.checked,
            onCheckedChange = null,
            thumbContent = {
              if (endContent.checked) {
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
}

@Preview
@Composable
private fun RowCellStatesPreviewLight() {
  PreviewTheme(darkMode = false) {
    RowCellStatesPreviewContent()
  }
}

@Preview
@Composable
private fun RowCellStatesPreviewDark() {
  PreviewTheme(darkMode = true) {
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
        endContent = EndContent.Content(
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
        endContent = EndContent.Content(
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
        endContent = EndContent.Content(
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
      )
    }

    RowPreviewBox {
      RowCell(
        title = TextModel("With switch and avatar"),
        startContent = StartContent.Avatar(Res.drawable.hn_logo),
        endContent = EndContent.Switch(true),
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
