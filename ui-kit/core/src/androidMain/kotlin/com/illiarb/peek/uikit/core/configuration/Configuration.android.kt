package com.illiarb.peek.uikit.core.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public actual fun getScreenWidth(): Dp {
  return LocalWindowInfo.current.containerSize.width.dp
}

@Composable
public actual fun getScreenHeight(): Dp {
  return LocalWindowInfo.current.containerSize.height.dp
}
