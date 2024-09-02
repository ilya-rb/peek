package com.illiarb.catchup.core.arch

import com.slack.circuit.runtime.screen.Screen

expect class OpenUrlScreen(url: String) : Screen {
  val url: String
}