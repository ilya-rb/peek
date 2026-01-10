package com.illiarb.peek.init

import android.content.ComponentName
import android.content.Context
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import com.illiarb.peek.core.arch.di.AppScope
import com.illiarb.peek.links.UrlLauncher
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@Inject
@ContributesIntoSet(AppScope::class, binding = binding<AndroidAppInitializer>())
@Suppress("unused")
internal class CustomTabsInitializer(
  private val androidLinkManager: UrlLauncher,
) : AndroidAppInitializer {

  override val async: Boolean = true

  override val key: String = "CustomTabsInitializer"

  override suspend fun initialise(context: Context) {
    val packageName = CustomTabsClient.getPackageName(context, null)
    if (packageName == null) {
      // Custom tabs not supported
      return
    }

    val connection = object : CustomTabsServiceConnection() {
      override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        client.warmup(0L)
        androidLinkManager.setCustomTabsClient(client)
      }

      override fun onServiceDisconnected(name: ComponentName?) {
        androidLinkManager.resetCustomTabs()
      }
    }

    CustomTabsClient.bindCustomTabsService(context, packageName, connection)
  }
}
