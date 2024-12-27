package com.illiarb.catchup.features.reader

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.illiarb.catchup.core.data.Async
import com.illiarb.catchup.features.reader.ReaderScreen.Event
import com.illiarb.catchup.service.domain.Article
import com.illiarb.catchup.uikit.core.components.ArticleReaderLoading
import com.illiarb.catchup.uikit.core.components.ErrorStateKind
import com.illiarb.catchup.uikit.core.components.FullscreenErrorState
import com.illiarb.catchup.uikit.core.components.TopAppBarTitleLoading
import com.illiarb.catchup.uikit.core.components.WebView
import com.illiarb.catchup.uikit.core.configuration.getScreenWidth
import com.illiarb.catchup.uikit.resources.Res
import com.illiarb.catchup.uikit.resources.acsb_action_more
import com.illiarb.catchup.uikit.resources.acsb_action_open_in_browser
import com.illiarb.catchup.uikit.resources.acsb_action_summarize
import com.illiarb.catchup.uikit.resources.acsb_navigation_back
import com.illiarb.catchup.uikit.resources.reader_action_open_in_browser
import com.illiarb.catchup.uikit.resources.reader_action_summarize
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

@Inject
public class ReaderScreenFactory : Ui.Factory {
  override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
    return when (screen) {
      is ReaderScreen -> {
        ui<ReaderScreen.State> { state, modifier ->
          ReaderScreen(modifier, state)
        }
      }

      else -> null
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderScreen(modifier: Modifier, state: ReaderScreen.State) {
  val eventSink = state.eventSink
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

  val startColor = MaterialTheme.colorScheme.surfaceContainerLow
  val endColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
  val contentScrollState = rememberScrollState()
  val size = getScreenWidth() * LocalDensity.current.density
  val percent = (contentScrollState.value.toFloat() / contentScrollState.maxValue.toFloat()) * 100f
  val scrolledWidth = percent / 100f * size.value

  var pageLoaded by remember { mutableStateOf(false) }

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
              contentDescription = stringResource(Res.string.acsb_navigation_back),
            )
          }
        },
        actions = {
          Box {
            if (pageLoaded) {
              IconButton(
                onClick = { eventSink.invoke(Event.TopBarMenuClicked) },
                enabled = state.article is Async.Content,
              ) {
                Icon(
                  imageVector = Icons.Filled.MoreVert,
                  contentDescription = stringResource(Res.string.acsb_action_more),
                )
              }
            } else {
              CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.padding(end = 16.dp).size(24.dp)
              )
            }

            DropdownMenu(
              expanded = state.topBarPopupShowing,
              onDismissRequest = { eventSink.invoke(Event.TopBarMenuDismissed) },
            ) {
              DropdownMenuItem(
                text = { Text(text = stringResource(Res.string.reader_action_open_in_browser)) },
                onClick = { eventSink.invoke(Event.OpenInBrowserClicked) },
                trailingIcon = {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = stringResource(Res.string.acsb_action_open_in_browser),
                  )
                },
              )
              DropdownMenuItem(
                text = { Text(text = stringResource(Res.string.reader_action_summarize)) },
                onClick = { eventSink.invoke(Event.SummarizeClicked) },
                trailingIcon = {
                  Icon(
                    imageVector = Icons.Filled.Summarize,
                    contentDescription = stringResource(Res.string.acsb_action_summarize),
                  )
                }
              )
            }
          }
        },
        title = {
          when (val content = state.article) {
            is Async.Loading, is Async.Error -> TopAppBarTitleLoading()
            is Async.Content -> {
              Column {
                Text(
                  text = content.content.title,
                  style = MaterialTheme.typography.bodyLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = content.content.link.url,
                  style = MaterialTheme.typography.bodySmall,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
              }
            }
          }
        },
      )
    },
  ) { innerPadding ->
    Box(Modifier.fillMaxSize().padding(innerPadding)) {
      when (state.article) {
        is Async.Loading -> {
          ArticleReaderLoading()
        }

        is Async.Error -> FullscreenErrorState(errorType = ErrorStateKind.UNKNOWN) {
          eventSink.invoke(Event.ErrorRetryClicked)
        }

        is Async.Content -> {
          ArticleContent(
            article = state.article.content,
            scrollState = contentScrollState,
            onPageLoaded = { pageLoaded = true }
          )
        }
      }
    }
  }
}

@Composable
private fun ArticleContent(
  modifier: Modifier = Modifier,
  article: Article,
  scrollState: ScrollState,
  onPageLoaded: () -> Unit,
) {
  WebView(
    url = article.link.url,
    onPageLoaded = onPageLoaded,
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState),
  )
}