package com.illiarb.catchup.features.reader

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SyncLock
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.reader.ReaderScreenContract.Event
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.service.domain.Url
import com.illiarb.catchup.uikit.core.components.ArticleReaderLoading
import com.illiarb.catchup.uikit.core.components.ErrorStateKind
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import com.illiarb.catchup.uikit.core.components.FullscreenState
import com.illiarb.catchup.uikit.core.components.HtmlView
import com.illiarb.catchup.uikit.core.components.LocalLottieAnimation
import com.illiarb.catchup.uikit.core.configuration.getScreenWidth
import com.illiarb.catchup.uikit.core.text.ReadingTimeText
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject

@Inject
class Factory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is ReaderScreenContract.ReaderScreen -> {
        ui<ReaderScreenContract.State> { state, modifier ->
          ReaderScreen(modifier, state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(modifier: Modifier, state: ReaderScreenContract.State) {
  val eventSink = state.eventSink
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  val startColor = MaterialTheme.colorScheme.surfaceContainerLow
  val endColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
  val contentScrollState = rememberScrollState()
  val size = getScreenWidth() * LocalDensity.current.density
  val percent = (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat()) * 100f
  val scrolledWidth = percent / 100f * size.value

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        modifier = Modifier.drawWithContent {
          drawContent()
          drawRect(
            brush = SolidColor(endColor),
            size = Size(
              height = this.size.height,
              width = scrolledWidth.coerceIn(0f, this.size.width),
            )
          )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
          scrolledContainerColor = startColor,
        ),
        navigationIcon = {
          IconButton(onClick = { eventSink.invoke(Event.NavigationIconClicked) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Go back",
            )
          }
        },
        title = {},
      )
    },
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      when (state.article) {
        is Async.Loading -> ArticleReaderLoading()
        is Async.Error -> FullscreenErrorState(errorType = ErrorStateKind.UNKNOWN) {
          eventSink.invoke(Event.ErrorRetryClicked)
        }

        is Async.Content -> ArticleContent(
          article = state.article.content,
          scrollState = contentScrollState,
          onLinkClicked = { url ->
            eventSink.invoke(Event.LinkClicked(url))
          }
        )
      }
    }
  }
}

@Composable
private fun ArticleContent(
  modifier: Modifier = Modifier,
  article: Article,
  scrollState: ScrollState,
  onLinkClicked: (Url) -> Unit,
) {
  val content = article.content
  requireNotNull(content)

  Column(
    modifier = modifier
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp)
      .navigationBarsPadding()
  ) {
    Text(
      text = article.title,
      style = MaterialTheme.typography.headlineMedium,
    )

    Text(
      text = article.link.url,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      textDecoration = TextDecoration.Underline,
      modifier = Modifier
        .padding(top = 8.dp)
        .clickable { onLinkClicked(article.link) },
    )

    Row(
      modifier = Modifier.padding(top = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Wed, Jan 18",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      Spacer(Modifier.weight(1f))

      Icon(
        modifier = Modifier.size(16.dp),
        imageVector = Icons.Outlined.Timer,
        contentDescription = "Estimated reading time"
      )
      ReadingTimeText(
        modifier = Modifier.padding(start = 8.dp),
        duration = content.estimatedReadingTime,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    HtmlView(
      content = content.text,
      style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
      modifier = Modifier
        .padding(vertical = 16.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(16.dp),
    )
  }
}