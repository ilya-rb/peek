package com.illiarb.catchup.uikit.core.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public actual fun getScreenWidth(): Dp {
  return LocalConfiguration.current.screenWidthDp.dp
}