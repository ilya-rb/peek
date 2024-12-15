package com.illiarb.catchup.uikit.core.components

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.illiarb.catchup.uikit.resources.R

@Composable
public actual fun HtmlView(modifier: Modifier, content: String, style: TextStyle) {
  val ctx = LocalContext.current
  val font by remember {
    mutableStateOf(ResourcesCompat.getFont(ctx, R.font.montserrat_normal))
  }

  AndroidView(
    modifier = modifier,
    update = {
      it.text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
    },
    factory = { context ->
      TextView(context).apply {
        setTextColor(style.color.toArgb())
        textSize = style.fontSize.value
        typeface = font
      }
    }
  )
}