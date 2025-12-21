package com.illiarb.peek.uikit.core.configuration

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import platform.UIKit.UIScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public actual fun getScreenWidth(): Dp {
  return LocalWindowInfo.current.containerSize.width.pxToPoint().dp
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
public actual fun getScreenHeight(): Dp {
  return LocalWindowInfo.current.containerSize.height.pxToPoint().dp
}

private fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale()
