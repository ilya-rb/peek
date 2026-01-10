package com.illiarb.peek.links

import android.content.Context
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.net.toUri
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.core.types.Url
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@Inject
internal class UrlLauncher {

  private var customTabsSession: CustomTabsSession? = null
  private var customTabsClient: CustomTabsClient? = null

  fun openUrl(context: Context, url: Url): Boolean {
    val scheme = CustomTabColorSchemeParams.Builder()
      .setToolbarColor(0x000000)
      .build()

    CustomTabsIntent.Builder(customTabsSession)
      .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, scheme)
      .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_LIGHT, scheme)
      .setShowTitle(true)
      .build()
      .launchUrl(context, url.url.toUri())

    return true
  }

  fun setCustomTabsClient(client: CustomTabsClient) {
    customTabsClient = client
    customTabsSession = client.newSession(CustomTabsCallback())
  }

  fun resetCustomTabs() {
    customTabsSession = null
    customTabsClient = null
  }
}
