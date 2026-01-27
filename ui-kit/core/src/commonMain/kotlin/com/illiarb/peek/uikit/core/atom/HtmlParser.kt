package com.illiarb.peek.uikit.core.atom

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.TextNode

private val BLOCK_TAGS = setOf(
  "p", "div", "blockquote", "ul", "ol", "li", "pre",
  "h1", "h2", "h3", "h4", "h5", "h6",
  "hr", "br", "img", "figure", "figcaption",
  "details", "summary", "section", "article", "header", "footer",
  "table", "thead", "tbody", "tr", "td", "th",
)

internal fun parseHtml(html: String): List<HtmlNode> {
  val document = Ksoup.parse(html)
  return parseChildren(document.body())
}

/**
 * Parse the children of an element, producing block-level [HtmlNode]s.
 * Consecutive inline nodes are collected into a single [HtmlNode.Paragraph].
 */
private fun parseChildren(parent: Element): List<HtmlNode> {
  val nodes = mutableListOf<HtmlNode>()
  val pendingInline = mutableListOf<InlineSpan>()

  fun flushInline() {
    if (pendingInline.isNotEmpty()) {
      nodes.add(HtmlNode.TextBlock(pendingInline.toList()))
      pendingInline.clear()
    }
  }

  for (child in parent.childNodes()) {
    when {
      child is TextNode -> {
        val text = child.getWholeText()
        if (text.isNotBlank()) {
          pendingInline.add(InlineSpan.PlainText(text))
        }
      }

      child is Element -> {
        val tag = child.tagName().lowercase()
        if (tag in BLOCK_TAGS || containsBlockChildren(child)) {
          flushInline()
          nodes.addAll(parseBlockElement(child))
        } else {
          pendingInline.addAll(parseInlineElement(child))
        }
      }
    }
  }

  flushInline()
  return nodes
}

private fun containsBlockChildren(element: Element): Boolean {
  return element.children().any { it.tagName().lowercase() in BLOCK_TAGS }
}

@Suppress("CyclomaticComplexMethod")
private fun parseBlockElement(element: Element): List<HtmlNode> {
  return when (element.tagName().lowercase()) {
    "p" -> {
      if (containsBlockChildren(element)) {
        // <p> with nested block elements — parse recursively
        listOf(HtmlNode.Paragraph(emptyList())) + parseChildren(element)
      } else {
        listOf(HtmlNode.Paragraph(parseInlineChildren(element)))
      }
    }

    "ul" -> {
      val items = element.select("> li").map { parseListItemContent(it) }
      listOf(HtmlNode.UnorderedList(items))
    }

    "ol" -> {
      val items = element.select("> li").map { parseListItemContent(it) }
      listOf(HtmlNode.OrderedList(items))
    }

    "li" -> {
      // Standalone <li> outside list — treat as paragraph
      if (containsBlockChildren(element)) {
        parseChildren(element)
      } else {
        listOf(HtmlNode.Paragraph(parseInlineChildren(element)))
      }
    }

    "img" -> {
      val src = element.attr("src")
      if (src.isNotEmpty()) listOf(HtmlNode.Image(src)) else emptyList()
    }

    "br" -> listOf(HtmlNode.Break)
    "hr" -> listOf(HtmlNode.Break)

    "pre" -> {
      // <pre> may contain <code> — extract text preserving whitespace
      listOf(HtmlNode.CodeBlock(element.wholeText()))
    }

    "h1", "h2", "h3", "h4", "h5", "h6" -> {
      listOf(HtmlNode.Paragraph(parseInlineChildren(element)))
    }

    else -> {
      // Unknown block element (div, blockquote, section, etc.) — recurse
      parseChildren(element)
    }
  }
}

/**
 * Parse a `<li>` element. If it contains only inline content, return spans.
 * If it has block children, flatten to inline spans for list display.
 */
private fun parseListItemContent(element: Element): List<InlineSpan> {
  return if (containsBlockChildren(element)) {
    flattenToInline(element)
  } else {
    parseInlineChildren(element)
  }
}

/**
 * Flatten an element with mixed block/inline content into inline spans.
 * Block breaks become newlines.
 */
private fun flattenToInline(element: Element): List<InlineSpan> {
  val spans = mutableListOf<InlineSpan>()

  for (child in element.childNodes()) {
    when {
      child is TextNode -> {
        val text = child.getWholeText()
        if (text.isNotBlank()) {
          spans.add(InlineSpan.PlainText(text))
        }
      }

      child is Element -> {
        val tag = child.tagName().lowercase()
        when {
          tag == "br" -> spans.add(InlineSpan.PlainText("\n"))
          tag in BLOCK_TAGS -> {
            spans.add(InlineSpan.PlainText("\n"))
            spans.addAll(flattenToInline(child))
            spans.add(InlineSpan.PlainText("\n"))
          }
          else -> spans.addAll(parseInlineElement(child))
        }
      }
    }
  }

  return spans
}

private fun parseInlineChildren(element: Element): List<InlineSpan> {
  val spans = mutableListOf<InlineSpan>()

  for (child in element.childNodes()) {
    when {
      child is TextNode -> {
        val text = child.getWholeText()
        if (text.isNotEmpty()) {
          spans.add(InlineSpan.PlainText(text))
        }
      }

      child is Element -> {
        spans.addAll(parseInlineElement(child))
      }
    }
  }

  return spans
}

private fun parseInlineElement(element: Element): List<InlineSpan> {
  return when (element.tagName().lowercase()) {
    "b", "strong" -> listOf(InlineSpan.Bold(parseInlineChildren(element)))

    "a" -> {
      val href = element.attr("href")
      listOf(InlineSpan.Link(parseInlineChildren(element), href))
    }

    "code" -> listOf(InlineSpan.Code(element.text()))

    "br" -> listOf(InlineSpan.PlainText("\n"))

    "em", "i" -> parseInlineChildren(element)

    "img" -> {
      // Inline images — skip in text spans
      emptyList()
    }

    else -> parseInlineChildren(element)
  }
}
