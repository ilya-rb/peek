package com.illiarb.peek.features.navigation.map

import com.illiarb.peek.core.types.Url
import com.slack.circuit.runtime.screen.Screen

public expect class ShareScreen(url: Url) : Screen {
  public val url: Url
}

public expect class OpenUrlScreen(url: Url) : Screen {
  public val url: Url
}
