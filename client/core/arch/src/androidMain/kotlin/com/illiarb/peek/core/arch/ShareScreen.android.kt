package com.illiarb.peek.core.arch

import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.android.AndroidScreen
import kotlinx.parcelize.Parcelize

@Parcelize
public actual data class ShareScreen actual constructor(actual val url: String) : Screen,
  AndroidScreen