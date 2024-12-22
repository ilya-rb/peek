package com.illiarb.catchup.uikit.core.components

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat

@Composable
public actual fun WebView(modifier: Modifier, url: String) {
  AndroidView(
    modifier = modifier,
    update = { view ->
      view.loadUrl(url)
    },
    factory = { context ->
      WebView(context).apply {
        ViewCompat.setNestedScrollingEnabled(this, true)
      }
    }
  )
}