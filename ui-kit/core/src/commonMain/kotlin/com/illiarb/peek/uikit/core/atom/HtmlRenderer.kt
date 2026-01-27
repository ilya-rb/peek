package com.illiarb.peek.uikit.core.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.illiarb.peek.uikit.imageloader.UrlImage

@Composable
public fun HtmlRenderer(
  html: String,
  modifier: Modifier = Modifier,
  onLinkClick: ((String) -> Unit)? = null,
) {
  val nodes = remember(html) { parseHtml(html) }
  val textStyle = MaterialTheme.typography.bodyLarge
  val linkColor = MaterialTheme.colorScheme.primary

  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(0.dp),
  ) {
    for (node in nodes) {
      when (node) {
        is HtmlNode.Paragraph -> {
          Spacer(Modifier.height(8.dp))
          InlineContent(
            spans = node.children,
            textStyle = textStyle,
            linkColor = linkColor,
            onLinkClick = onLinkClick,
          )
          Spacer(Modifier.height(8.dp))
        }

        is HtmlNode.UnorderedList -> {
          Spacer(Modifier.height(4.dp))
          for (item in node.items) {
            Row(modifier = Modifier.padding(start = 8.dp)) {
              Text(
                text = "–  ",
                style = textStyle,
              )
              InlineContent(
                spans = item,
                textStyle = textStyle,
                linkColor = linkColor,
                onLinkClick = onLinkClick,
              )
            }
          }
          Spacer(Modifier.height(4.dp))
        }

        is HtmlNode.OrderedList -> {
          Spacer(Modifier.height(4.dp))
          for ((index, item) in node.items.withIndex()) {
            Row(modifier = Modifier.padding(start = 8.dp)) {
              Text(
                text = "${index + 1}.  ",
                style = textStyle,
              )
              InlineContent(
                spans = item,
                textStyle = textStyle,
                linkColor = linkColor,
                onLinkClick = onLinkClick,
              )
            }
          }
          Spacer(Modifier.height(4.dp))
        }

        is HtmlNode.Image -> {
          Spacer(Modifier.height(12.dp))
          UrlImage(
            url = node.src,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp)),
          )
          Spacer(Modifier.height(12.dp))
        }

        is HtmlNode.Break -> {
          Spacer(Modifier.height(8.dp))
        }

        is HtmlNode.CodeBlock -> {
          Text(
            text = node.code,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(12.dp),
          )
        }

        is HtmlNode.TextBlock -> {
          InlineContent(
            spans = node.children,
            textStyle = textStyle,
            linkColor = linkColor,
            onLinkClick = onLinkClick,
          )
        }
      }
    }
  }
}

@Composable
private fun InlineContent(
  spans: List<InlineSpan>,
  textStyle: TextStyle,
  linkColor: Color,
  onLinkClick: ((String) -> Unit)?,
) {
  val annotated = buildAnnotatedString {
    appendSpans(spans, linkColor)
  }

  if (onLinkClick != null && annotated.getStringAnnotations("URL", 0, annotated.length)
      .isNotEmpty()
  ) {
    @Suppress("DEPRECATION")
    ClickableText(
      text = annotated,
      style = textStyle.copy(color = MaterialTheme.colorScheme.onSurface),
      onClick = { offset ->
        annotated.getStringAnnotations("URL", offset, offset)
          .firstOrNull()
          ?.let { onLinkClick(it.item) }
      },
    )
  } else {
    Text(
      text = annotated,
      style = textStyle,
    )
  }
}

private fun AnnotatedString.Builder.appendSpans(spans: List<InlineSpan>, linkColor: Color) {
  for (span in spans) {
    when (span) {
      is InlineSpan.PlainText -> append(span.text)
      is InlineSpan.Bold -> {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        appendSpans(span.children, linkColor)
        pop()
      }

      is InlineSpan.Link -> {
        pushStyle(
          SpanStyle(
            color = linkColor,
            textDecoration = TextDecoration.Underline,
          )
        )
        pushStringAnnotation(tag = "URL", annotation = span.href)
        appendSpans(span.children, linkColor)
        pop()
        pop()
      }

      is InlineSpan.Code -> {
        pushStyle(SpanStyle(fontFamily = FontFamily.Monospace))
        append(span.text)
        pop()
      }
    }
  }
}

// --- HTML model ---

internal sealed interface HtmlNode {
  data class Paragraph(val children: List<InlineSpan>) : HtmlNode
  data class UnorderedList(val items: List<List<InlineSpan>>) : HtmlNode
  data class OrderedList(val items: List<List<InlineSpan>>) : HtmlNode
  data class Image(val src: String) : HtmlNode
  data class CodeBlock(val code: String) : HtmlNode
  data object Break : HtmlNode
  data class TextBlock(val children: List<InlineSpan>) : HtmlNode
}

internal sealed interface InlineSpan {
  data class PlainText(val text: String) : InlineSpan
  data class Bold(val children: List<InlineSpan>) : InlineSpan
  data class Link(val children: List<InlineSpan>, val href: String) : InlineSpan
  data class Code(val text: String) : InlineSpan
}
