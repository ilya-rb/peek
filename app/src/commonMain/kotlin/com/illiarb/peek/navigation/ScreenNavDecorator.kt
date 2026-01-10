package com.illiarb.peek.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.NavArgument
import com.slack.circuit.foundation.animation.AnimatedNavDecorator
import com.slack.circuit.foundation.animation.AnimatedNavEvent
import com.slack.circuit.foundation.animation.AnimatedNavState
import kotlin.uuid.ExperimentalUuidApi

@Stable
public class ScreenNavDecoration<T : NavArgument> : AnimatedNavDecorator<T, ScreenNavState<T>> {

  override fun AnimatedContentTransitionScope<AnimatedNavState>.transitionSpec(
    animatedNavEvent: AnimatedNavEvent
  ): ContentTransform {
    return when (animatedNavEvent) {
      AnimatedNavEvent.GoTo -> {
        val enterTransition = slideInHorizontally(tween(ANIMATION_DURATION)) { it }
        val exitTransition = slideOutHorizontally(tween(ANIMATION_DURATION)) { -it / 3 }

        ContentTransform(
          targetContentEnter = enterTransition,
          initialContentExit = exitTransition,
          targetContentZIndex = 1f, // New screen on top
        )
      }

      AnimatedNavEvent.Pop -> {
        val enterTransition = slideInHorizontally(tween(ANIMATION_DURATION)) { -it / 3 }
        val exitTransition = slideOutHorizontally(tween(ANIMATION_DURATION)) { it }

        ContentTransform(
          targetContentEnter = enterTransition,
          initialContentExit = exitTransition,
          targetContentZIndex = 0f, // Previous screen underneath
        )
      }

      else -> {
        fadeIn() togetherWith fadeOut()
      }
    }
  }

  override fun targetState(args: List<T>): ScreenNavState<T> {
    return ScreenNavState(args)
  }

  @Composable
  override fun updateTransition(args: List<T>): Transition<ScreenNavState<T>> {
    val targetState = targetState(args)
    return updateTransition(targetState = targetState, label = "ScreenNavDecoration")
  }

  @Composable
  override fun AnimatedContentScope.Decoration(
    targetState: ScreenNavState<T>,
    innerContent: @Composable ((T) -> Unit)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      innerContent(targetState.backStack.first())
    }
  }

  public companion object : AnimatedNavDecorator.Factory {
    private const val ANIMATION_DURATION = 300

    override fun <T : NavArgument> create(): AnimatedNavDecorator<T, *> {
      return ScreenNavDecoration()
    }
  }
}

@Stable
@OptIn(ExperimentalUuidApi::class)
public class ScreenNavState<T : NavArgument>(
  override val backStack: List<T>,
) : AnimatedNavState {

  // Use the first item's key as unique identity for AnimatedContent
  private val key: String = backStack.first().screen.hashCode().toString()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ScreenNavState<*>) return false
    return key == other.key
  }

  override fun hashCode(): Int = key.hashCode()
}
