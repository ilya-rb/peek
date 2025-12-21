package com.illiarb.peek.features.summarizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.components.PreviewTheme

@Composable
@Preview
internal fun SummaryLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    SummaryLoading()
  }
}

@Composable
@Preview
internal fun SummaryLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    SummaryLoading()
  }
}
