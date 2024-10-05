package presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay

// https://github.com/idapgroup/Pulsation/blob/master/pulsation/src/main/java/com/idapgroup/pulsation/Pulsation.kt

/**
 * Pulsation animation that duplicates content and animate it under it.
 * @param enabled - if true uses the given parameters and start the animation.
 * @param type - set of animation params to be used for animated content
 * @param content - composable content that will be drawn and copied for animation.
 */
@Composable
public fun Pulsation(
  enabled: Boolean,
  type: PulsationType,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  Pulsation(
    enabled = enabled,
    repeatsCount = type.repeatsCount,
    iterations = type.iterations,
    iterationDuration = type.iterationDuration,
    iterationDelay = type.iterationDelay,
    delayBetweenRepeats = type.delayBetweenRepeats,
    alphaRange = type.alphaRange,
    pulseRange = type.pulseRange,
    contentType = type.contentType,
    wavesCount = type.wavesCount,
    modifier = modifier,
    content = content
  )
}

/**
 * Pulsation animation that duplicates content and animate it under it.
 * Core function that gives more options to modify.
 * @param enabled - if true uses the given parameters and start the animation.
 * @param repeatsCount - amount of animation repeats. Use [Int.MAX_VALUE] for infinite repeats
 * @param delayBetweenRepeats - milliseconds between each repeat of animation cycle.
 * @param iterations - a cycle of repetitive animations.
 * @param iterationDuration - duration of 1 animation.
 * @param iterationDelay - delay between iterations in 1 animation cycle.
 * @param pulseRange - range between minimum and maximum animated [content] scale.
 * @param alphaRange - range between started and ended alpha for animated [content].
 * @param contentType - type of animated view.
 * @param content - composable content that will be drawn and copied for animation.
 */
@Composable
public fun Pulsation(
  enabled: Boolean,
  repeatsCount: Int,
  delayBetweenRepeats: Int,
  iterations: Int,
  iterationDuration: Int,
  iterationDelay: Int,
  wavesCount: Int,
  pulseRange: ClosedFloatingPointRange<Float>,
  alphaRange: ClosedFloatingPointRange<Float>,
  contentType: ContentType,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val animationHolder = remember {
    List(wavesCount) {
      AnimationHolder(
        scale = Animatable(pulseRange.start),
        alpha = Animatable(alphaRange.start)
      )
    }
  }
  LaunchedEffect(animationHolder, enabled) {
    while (enabled) {
      var count = 0
      animationHolder.flatMapIndexed { index, (scale, alpha) ->
        listOf(
          async {
            delay((index * (iterationDuration / wavesCount)).toLong())
            scale.snapTo(pulseRange.start)
            scale.animateTo(
              targetValue = pulseRange.endInclusive,
              animationSpec = repeatable(
                iterations = iterations,
                animation = tween(
                  durationMillis = iterationDuration,
                  delayMillis = iterationDelay
                ),
                repeatMode = RepeatMode.Restart
              )
            )
            scale.snapTo(pulseRange.start)
          },
          async {
            delay((index * (iterationDuration / wavesCount)).toLong())
            alpha.snapTo(alphaRange.start)
            alpha.animateTo(
              targetValue = alphaRange.endInclusive,
              animationSpec = repeatable(
                iterations = iterations,
                animation = tween(
                  durationMillis = iterationDuration,
                  delayMillis = iterationDelay
                ),
                repeatMode = RepeatMode.Restart
              )
            )
          }
        )
      }.awaitAll()
      count += 1
      if (repeatsCount <= count) {
        return@LaunchedEffect
      }
      delay(delayBetweenRepeats.toLong())
    }
    animationHolder.forEach { (scale, alpha) ->
      scale.snapTo(pulseRange.start)
      alpha.snapTo(alphaRange.start)
    }
  }
  var size: IntSize by remember {
    mutableStateOf(IntSize(0, 0))
  }
  val dpSize = with(LocalDensity.current) {
    DpSize(width = size.width.toDp(), height = size.height.toDp())
  }
  Box(modifier = modifier) {
    animationHolder.forEachIndexed { _, (scale, alpha) ->
      Box(
        modifier = Modifier
          .scale(scale.value)
          .alpha(alpha.value)
      ) {
        when (contentType) {
          is ContentType.Colored -> {
            Box(
              modifier = Modifier
                .size(dpSize)
                .background(contentType.color, shape = contentType.shape)
            )
          }

          ContentType.ContentTwin -> content()
          is ContentType.Gradient -> {
            Box(
              modifier = Modifier
                .size(dpSize)
                .background(contentType.brush, shape = contentType.shape)
            )
          }
        }
      }
    }

    Box(
      modifier = Modifier.onGloballyPositioned {
        size = it.size
      }
    ) {
      content()
    }
  }
}

/**
 * Top level declaration of animation type for simplified usages of animation params
 */
public sealed class PulsationType(
  public val repeatsCount: Int,
  public val iterations: Int,
  public val iterationDuration: Int,
  public val iterationDelay: Int,
  public val delayBetweenRepeats: Int,
  public val contentType: ContentType,
  public val wavesCount: Int = 1,
  public val pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
  public val alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
) {
  /**
   * Linear animation type
   */
  public class Linear(
    repeatsCount: Int = Int.MAX_VALUE,
    duration: Int = 500,
    delayBetweenRepeats: Int = 0,
    contentType: ContentType = ContentType.ContentTwin,
    pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
    alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
  ) : PulsationType(
    repeatsCount = repeatsCount,
    iterations = 1,
    iterationDuration = duration,
    iterationDelay = 0,
    delayBetweenRepeats = delayBetweenRepeats,
    pulseRange = pulseRange,
    contentType = contentType,
    alphaRange = alphaRange
  )

  /**
   * Added possibility to make animation cycles inside animation process.
   */
  public class Iterative(
    repeatCount: Int = Int.MAX_VALUE,
    iterations: Int = 3,
    iterationDuration: Int = 500,
    iterationDelay: Int = 0,
    delayBetweenRepeats: Int = 500,
    contentType: ContentType = ContentType.ContentTwin,
    pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
    alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
  ) : PulsationType(
    repeatsCount = repeatCount,
    iterations = iterations,
    iterationDuration = iterationDuration,
    iterationDelay = iterationDelay,
    delayBetweenRepeats = delayBetweenRepeats,
    pulseRange = pulseRange,
    alphaRange = alphaRange,
    contentType = contentType,
  )

  /**
   * Start of next animations before end of previous
   */
  public class Races(
    duration: Int = 500,
    wavesCount: Int = 5,
    contentType: ContentType = ContentType.ContentTwin,
    pulseRange: ClosedFloatingPointRange<Float> = 1f..1.4f,
    alphaRange: ClosedFloatingPointRange<Float> = 1f..0f,
  ) : PulsationType(
    repeatsCount = Int.MAX_VALUE,
    iterations = 1,
    iterationDuration = duration,
    iterationDelay = 0, // duration.div(wavesCount),
    wavesCount = wavesCount,
    delayBetweenRepeats = 0,
    pulseRange = pulseRange,
    alphaRange = alphaRange,
    contentType = contentType,
  )
}

/**
 * Adds possibility to change animated pulsation background object
 */
public sealed interface ContentType {

  /**
   * Creates animated object with defined color and shape.
   */
  public class Colored(public val color: Color, public val shape: Shape = RectangleShape) : ContentType

  /**
   * Creates animated object with defined brush and shape
   */
  public class Gradient(public val brush: Brush, public val shape: Shape = RectangleShape) : ContentType

  /**
   * Creates the same animated object as a given content
   */
  public data object ContentTwin : ContentType
}

internal data class AnimationHolder(
  val scale: Animatable<Float, AnimationVector1D>,
  val alpha: Animatable<Float, AnimationVector1D>
)
