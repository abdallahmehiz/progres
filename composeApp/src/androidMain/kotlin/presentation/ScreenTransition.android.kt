package presentation

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransitionContent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

// https://gist.github.com/ivaniskandar/af98c91d20a3124c6592bd7a830c0334

@Suppress("CyclomaticComplexMethod")
@Composable
actual fun SlideScreenTransition(
  navigator: Navigator,
  modifier: Modifier,
  enterTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> EnterTransition,
  exitTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> ExitTransition,
  popEnterTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> EnterTransition,
  popExitTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> ExitTransition,
  sizeTransform: (AnimatedContentTransitionScope<Screen>.() -> SizeTransform?)?,
  flingAnimationSpec: () -> AnimationSpec<Float>,
  content: ScreenTransitionContent
) {
  val scope = rememberCoroutineScope()
  var animationJob by remember { mutableStateOf<Pair<Job, AnimationType>?>(null) }

  var progress by remember { mutableFloatStateOf(0f) }
  var swipeEdge by remember { mutableStateOf(SwipeEdge.Unknown) }
  var isPredictiveBack by remember { mutableStateOf(false) }
  val transition = if (sizeTransform != null) {
    val transitionState = remember { SeekableTransitionState(navigator.lastItem) }
    LaunchedEffect(progress) {
      val previousEntry = navigator.items.getOrNull(navigator.size - 2)
      if (previousEntry != null) transitionState.seekTo(targetState = previousEntry, fraction = progress)
    }
    LaunchedEffect(navigator) {
      snapshotFlow { navigator.lastItem }
        .collect {
          if (animationJob?.second == AnimationType.Cancel) animationJob?.first?.cancel()
          transitionState.animateTo(it)
        }
    }
    rememberTransition(transitionState = transitionState)
  } else {
    if (isPredictiveBack) {
      val transitionState = remember { SeekableTransitionState(navigator.lastItem) }
      LaunchedEffect(progress) {
        val previousEntry = navigator.items.getOrNull(navigator.size - 2)
        if (previousEntry != null) transitionState.seekTo(targetState = previousEntry, fraction = progress)
      }
      rememberTransition(transitionState = transitionState)
    } else {
      updateTransition(targetState = navigator.lastItem)
    }
  }

  PredictiveBackHandler(enabled = navigator.canPop) { backEvent ->
    if (animationJob?.second == AnimationType.Cancel) animationJob?.first?.cancel()
    try {
      backEvent
        .dropWhile { animationJob != null }
        .collect {
          swipeEdge = when (it.swipeEdge) {
            BackEventCompat.EDGE_LEFT -> SwipeEdge.Left
            BackEventCompat.EDGE_RIGHT -> SwipeEdge.Right
            else -> SwipeEdge.Unknown
          }
          progress = LinearOutSlowInEasing.transform(it.progress)
          isPredictiveBack = true
        }
      animationJob = scope.launch {
        try {
          if (isPredictiveBack) {
            animate(
              initialValue = progress,
              targetValue = 1f,
              animationSpec = flingAnimationSpec(),
              block = { i, _ -> progress = i },
            )
          }
          navigator.pop()
        } catch (e: CancellationException) {
          // Cancelled
          progress = 0f
        } finally {
          isPredictiveBack = false
          swipeEdge = SwipeEdge.Unknown
          animationJob = null
        }
      } to AnimationType.Pop
    } catch (e: CancellationException) {
      animationJob = scope.launch {
        try {
          if (isPredictiveBack) {
            animate(
              initialValue = progress,
              targetValue = 0f,
              animationSpec = flingAnimationSpec(),
              block = { i, _ -> progress = i },
            )
          }
        } catch (e: CancellationException) {
          progress = 1f
        } finally {
          isPredictiveBack = false
          swipeEdge = SwipeEdge.Unknown
          animationJob = null
        }
      } to AnimationType.Cancel
    }
  }

  transition.AnimatedContent(
    modifier = modifier,
    transitionSpec = {
      val pop = navigator.lastEvent == StackEvent.Pop || isPredictiveBack
      ContentTransform(
        targetContentEnter = if (pop) popEnterTransition(swipeEdge) else enterTransition(swipeEdge),
        initialContentExit = if (pop) popExitTransition(swipeEdge) else exitTransition(swipeEdge),
        targetContentZIndex = if (pop) 0f else 1f,
        sizeTransform = sizeTransform?.invoke(this),
      )
    },
    contentKey = { it.key },
  ) {
    navigator.saveableState("transition", it) {
      content(it)
    }
  }
}
