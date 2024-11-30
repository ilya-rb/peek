package com.illiarb.catchup.uikit.core.painter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp

class TextWithBackgroundPainter(
  text: String,
  textStyle: TextStyle,
  textMeasurer: TextMeasurer,
  private val backgroundColor: Color,
) : Painter() {

  private val textResult: TextLayoutResult = textMeasurer.measure(
    text = AnnotatedString(text),
    style = textStyle.copy(textAlign = TextAlign.Center, fontSize = 14.sp),
    maxLines = 1,
  )

  override val intrinsicSize: Size = Size.Unspecified

  override fun DrawScope.onDraw() {
    drawCircle(color = backgroundColor)

    drawText(
      textLayoutResult = textResult,
      brush = SolidColor(Color.White),
      topLeft = Offset(
        x = (size.width - textResult.size.width) / 2,
        y = (size.height - textResult.size.height) / 2,
      )
    )
  }
}