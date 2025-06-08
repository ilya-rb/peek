package com.illiarb.catchup.uikit.core.preview.components.cell

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.uikit.core.components.TopAppBarTitleLoading
import com.illiarb.catchup.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun TopAppBarTitleLoadingPreviewLight() {
  TopAppBarTitleContents(darkMode = false)
}

@Composable
@Preview
internal fun TopAppBarTitleLoadingPreviewDark() {
  TopAppBarTitleContents(darkMode = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarTitleContents(darkMode: Boolean) {
  PreviewTheme(darkMode) {
    TopAppBar(
      title = {
        TopAppBarTitleLoading()
      }
    )
  }
}