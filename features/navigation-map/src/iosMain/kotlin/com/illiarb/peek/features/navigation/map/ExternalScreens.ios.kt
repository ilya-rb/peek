package com.illiarb.peek.features.navigation.map

import com.illiarb.peek.core.types.Url
import com.slack.circuit.runtime.screen.Screen

public actual data class OpenUrlScreen actual constructor(actual val url: Url) : Screen

public actual data class ShareScreen actual constructor(actual val url: Url) : Screen