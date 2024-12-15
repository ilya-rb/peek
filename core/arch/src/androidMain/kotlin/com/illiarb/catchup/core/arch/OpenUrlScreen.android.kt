package com.illiarb.catchup.core.arch

import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.android.AndroidScreen
import kotlinx.parcelize.Parcelize

@Parcelize
public actual data class OpenUrlScreen actual constructor(actual val url: String) : Screen, AndroidScreen