package presentation.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// https://gist.github.com/stevdza-san/cca20eff9f2c4c7d783ffd0a0061b352
sealed class RequestState<out T> {
  data object Idle : RequestState<Nothing>()
  data object Loading : RequestState<Nothing>()
  data class Success<T>(val data: T) : RequestState<T>()
  data class Error(val throwable: Throwable) : RequestState<Nothing>()

  fun isLoading() = this is Loading
  fun isSuccess() = this is Success
  fun isError() = this is Error

  /**
   * Returns data from a [Success].
   * @throws ClassCastException If the current state is not [Success]
   *  */
  fun getSuccessData() = (this as Success).data
  fun getSuccessDataOrNull(): T? {
    return try {
      (this as Success).data
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Returns an error message from an [Error]
   * @throws ClassCastException If the current state is not [Error]
   *  */
  fun getErrorMessage() = (this as Error).throwable.message
  fun getErrorMessageOrNull(): String? {
    return try {
      (this as Error).throwable.message
    } catch (e: Exception) {
      null
    }
  }

  @Composable
  fun DisplayResult(
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (Throwable) -> Unit,
    modifier: Modifier = Modifier,
    onIdle: (@Composable () -> Unit)? = null,
  ) {
    AnimatedContent(
      targetState = this@RequestState,
      transitionSpec = {
        fadeIn(tween(durationMillis = 300)) togetherWith fadeOut(tween(durationMillis = 300))
      },
      label = "Content Animation",
      modifier = modifier,
    ) { state ->
      when (state) {
        is Idle -> {
          onIdle?.invoke()
        }

        is Loading -> {
          onLoading()
        }

        is Success -> {
          onSuccess(state.getSuccessData())
        }

        is Error -> {
          onError(state.throwable)
        }
      }
    }
  }
}
