package com.illiarb.catchup.summarizer.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.illiarb.catchup.summarizer.ui.internal.SummaryLoading
import com.illiarb.catchup.uikit.core.preview.components.PreviewTheme

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