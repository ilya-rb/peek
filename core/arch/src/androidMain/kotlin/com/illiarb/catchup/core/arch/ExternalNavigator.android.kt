package com.illiarb.catchup.core.arch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import me.tatarka.inject.annotations.Inject

class AndroidExternalNavigator @Inject constructor(
  private val activity: Activity,
) : ExternalNavigator {

  override fun openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setData(Uri.parse(url))
    }
    activity.startActivity(intent)
  }
}
