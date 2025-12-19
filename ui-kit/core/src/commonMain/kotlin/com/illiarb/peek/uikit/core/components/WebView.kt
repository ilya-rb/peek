package com.illiarb.peek.uikit.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
public expect fun WebView(modifier: Modifier, url: String, onPageLoaded: () -> Unit = {})