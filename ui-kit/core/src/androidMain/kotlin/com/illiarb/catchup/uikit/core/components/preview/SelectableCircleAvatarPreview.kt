package com.illiarb.catchup.uikit.core.components.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.SelectableCircleAvatarLoading

@Composable
@Preview(showBackground = true)
internal fun SelectableCircleAvatarPreview() {
  SelectableCircleAvatarLoading(selected = true)
}
