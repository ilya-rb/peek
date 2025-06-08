package com.illiarb.catchup.uikit.core.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

@Composable
public actual fun WebView(
  modifier: Modifier,
  url: String,
  onPageLoaded: () -> Unit,
) {
  val systemInDarkTheme = isSystemInDarkTheme()

  AndroidView(
    modifier = modifier,
    update = { view ->
      view.settings.javaScriptEnabled = false
      view.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          onPageLoaded()
        }
      }

      if (systemInDarkTheme && WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
        WebSettingsCompat.setAlgorithmicDarkeningAllowed(view.settings, true)
      }

      view.loadUrl(url)
    },
    factory = { context ->
      WebView(context).apply {
        ViewCompat.setNestedScrollingEnabled(this, true)
      }
    },
  )
}