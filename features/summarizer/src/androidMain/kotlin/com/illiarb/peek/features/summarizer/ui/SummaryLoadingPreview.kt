package com.illiarb.peek.features.summarizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.peek.uikit.core.preview.PreviewTheme

@Composable
@Preview
internal fun SummaryLoadingPreviewLight() {
  PreviewTheme(darkMode = false) {
    SummaryLoading(Modifier)
  }
}

@Composable
@Preview
internal fun SummaryLoadingPreviewDark() {
  PreviewTheme(darkMode = true) {
    SummaryLoading(Modifier)
  }
}
