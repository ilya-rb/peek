package com.illiarb.peek.core.appinfo.internal

import android.os.Build

internal actual fun isAndroidQ(): Boolean {
  return Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
}
