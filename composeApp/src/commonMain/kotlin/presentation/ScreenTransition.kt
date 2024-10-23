package presentation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransitionContent

@Composable
expect fun SlideScreenTransition(
  navigator: Navigator,
  modifier: Modifier = Modifier,
  enterTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> EnterTransition = {
    slideInHorizontally { it }
  },
  exitTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> ExitTransition = {
    slideOutHorizontally { -it }
  },
  popEnterTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> EnterTransition = {
    slideInHorizontally { -it }
  },
  popExitTransition: AnimatedContentTransitionScope<Screen>.(SwipeEdge) -> ExitTransition = {
    slideOutHorizontally { it }
  },
  sizeTransform: (AnimatedContentTransitionScope<Screen>.() -> SizeTransform?)? = null,
  flingAnimationSpec: () -> AnimationSpec<Float> = { spring(stiffness = Spring.StiffnessLow) },
  content: ScreenTransitionContent = { it.Content() },
)

enum class SwipeEdge {
  Unknown,
  Left,
  Right,
}

enum class AnimationType {
  Pop, Cancel
}
