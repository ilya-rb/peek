package com.illiarb.peek.core.arch

import com.slack.circuit.runtime.screen.Screen

public expect class ShareScreen(url: String) : Screen {
  public val url: String
}